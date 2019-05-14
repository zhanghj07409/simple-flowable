/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.app.service.editor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.zip.ZipInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.flowable.app.domain.editor.AppDefinition;
import org.flowable.app.domain.editor.Model;
import org.flowable.app.service.api.AppDefinitionService;
import org.flowable.app.service.exception.InternalServerErrorException;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.common.api.FlowableException;
import org.flowable.engine.common.api.FlowableIllegalArgumentException;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.idm.api.User;
import org.flowable.rest.service.api.RestResponseFactory;
import org.flowable.rest.service.api.repository.DeploymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Can't merge this with {@link AppDefinitionService}, as it doesn't have visibility of domain models needed to do the publication.
 * 
 * @author jbarrez
 */
@Service
@Transactional
public class AppDefinitionPublishService extends BaseAppDefinitionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppDefinitionPublishService.class);

    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    protected RestResponseFactory restResponseFactory;

    @Autowired
    protected Environment environment;

    public void publishAppDefinition(String comment, Model appDefinitionModel, User user) {

        // Create new version of the app model
        modelService.createNewModelVersion(appDefinitionModel, comment, user);

        String deployableZipName = appDefinitionModel.getKey() + ".zip";

        AppDefinition appDefinition = null;
        try {
            appDefinition = resolveAppDefinition(appDefinitionModel);
        } catch (Exception e) {
            LOGGER.error("Error deserializing app {}", appDefinitionModel.getId(), e);
            throw new InternalServerErrorException("Could not deserialize app definition");
        }

        if (appDefinition != null) {
            byte[] deployZipArtifact = createDeployableZipArtifact(appDefinitionModel, appDefinition);

            if (deployZipArtifact != null) {
                //deployZipArtifact(deployableZipName, deployZipArtifact, appDefinitionModel.getKey(), appDefinitionModel.getName());
                changeDeployZipArtifact(deployableZipName, deployZipArtifact, appDefinitionModel.getKey(), appDefinitionModel.getName());
            }
        }
    }

    protected DeploymentResponse changeDeployZipArtifact(String artifactName, byte[] zipArtifact, String deploymentKey, String deploymentName) {
        if(zipArtifact.length == 0) {
            throw new FlowableIllegalArgumentException("Multipart request with file content is required");
        } else {
            try {
                DeploymentBuilder e = repositoryService.createDeployment();

                if(!artifactName.endsWith(".bpmn20.xml") && !artifactName.endsWith(".bpmn")) {
                    if(!artifactName.toLowerCase().endsWith(".bar") && !artifactName.toLowerCase().endsWith(".zip")) {
                        throw new FlowableIllegalArgumentException("File must be of type .bpmn20.xml, .bpmn, .bar or .zip");
                    }

                    e.addZipInputStream(new ZipInputStream(new ByteArrayInputStream(zipArtifact)));
                } else {
                    e.addInputStream(artifactName, new ByteArrayInputStream(zipArtifact));
                }

                if(!StringUtils.isEmpty(deploymentName)) {
                    e.name(deploymentName);
                } else {
                    String deployment = artifactName.split("\\.")[0];
                    if(StringUtils.isNotEmpty(deployment)) {
                        artifactName = deployment;
                    }

                    e.name(artifactName);
                }

                if(StringUtils.isNotEmpty(deploymentKey)) {
                    e.key(deploymentKey);
                }

                Deployment deployment1 = e.deploy();
                return restResponseFactory.createDeploymentResponse(deployment1);
            } catch (Exception var13) {
                if(var13 instanceof FlowableException) {
                    throw (FlowableException)var13;
                } else {
                    throw new FlowableException(var13.getMessage(), var13);
                }
            }
        }
    }

    @Deprecated
    protected void deployZipArtifact(String artifactName, byte[] zipArtifact, String deploymentKey, String deploymentName) {
        String deployApiUrl = environment.getRequiredProperty("deployment.api.url");
        String basicAuthUser = environment.getRequiredProperty("idm.admin.user");
        String basicAuthPassword = environment.getRequiredProperty("idm.admin.password");

        if (!deployApiUrl.endsWith("/")) {
            deployApiUrl = deployApiUrl.concat("/");
        }
        deployApiUrl = deployApiUrl.concat(String.format("repository/deployments?deploymentKey=%s&deploymentName=%s",
                encode(deploymentKey), encode(deploymentName)));

        HttpPost httpPost = new HttpPost(deployApiUrl);
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + new String(
                Base64.encodeBase64((basicAuthUser + ":" + basicAuthPassword).getBytes(Charset.forName("UTF-8")))));

        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        entityBuilder.addBinaryBody("artifact", zipArtifact, ContentType.DEFAULT_BINARY, artifactName);

        HttpEntity entity = entityBuilder.build();
        httpPost.setEntity(entity);

        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            clientBuilder.setSSLSocketFactory(
                    new SSLConnectionSocketFactory(builder.build(), new HostnameVerifier() {
                        @Override
                        public boolean verify(String s, SSLSession sslSession) {
                            return true;
                        }
                    })
            );

        } catch (Exception e) {
            LOGGER.error("Could not configure SSL for http client", e);
            throw new InternalServerErrorException("Could not configure SSL for http client", e);
        }

        CloseableHttpClient client = clientBuilder.build();

        try {
            HttpResponse response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
                return;
            } else {
                LOGGER.error("Invalid deploy result code: {}", response.getStatusLine());
                throw new InternalServerErrorException("Invalid deploy result code: " + response.getStatusLine());
            }

        } catch (IOException ioe) {
            LOGGER.error("Error calling deploy endpoint", ioe);
            throw new InternalServerErrorException("Error calling deploy endpoint: " + ioe.getMessage());
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    LOGGER.warn("Exception while closing http client", e);
                }
            }
        }
    }

    protected String encode(String string) {
        if (string != null) {
            try {
                return URLEncoder.encode(string, "UTF-8");
            } catch (UnsupportedEncodingException uee) {
                throw new IllegalStateException("JVM does not support UTF-8 encoding.", uee);
            }
        }
        return null;
    }
}

