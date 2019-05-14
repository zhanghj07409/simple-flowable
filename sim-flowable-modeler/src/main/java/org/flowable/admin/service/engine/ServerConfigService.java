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
package org.flowable.admin.service.engine;

import org.flowable.admin.domain.EndpointType;
import org.flowable.admin.domain.ServerConfig;
import org.flowable.admin.dto.ServerConfigRepresentation;
import org.flowable.admin.repository.ServerConfigRepository;
import org.flowable.admin.service.engine.exception.FlowableServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jbarrez
 * @author Yvo Swillens
 */
@Service
public class ServerConfigService extends AbstractEncryptingService {

    /**
     * PROCESS
     */
    @Value("${admin.rest.process.app.name}")
    private String restProcessAppName;

    @Value("${admin.rest.process.app.description}")
    private String restProcessAppDescription;

    @Value("${admin.rest.process.app.host}")
    private String restProcessAppHost;

    @Value("${admin.rest.process.app.port}")
    private Integer restProcessAppPort;

    @Value("${admin.rest.process.app.contextroot}")
    private String restProcessAppContextRoot;

    @Value("${admin.rest.process.app.restroot}")
    private String restProcessAppRestRoot;

    /**
     * DMN
     */
    @Value("${admin.rest.dmn.app.name}")
    private String restDmnAppName;

    @Value("${admin.rest.dmn.app.description}")
    private String restDmnAppDescription;

    @Value("${admin.rest.dmn.app.host}")
    private String restDmnAppHost;

    @Value("${admin.rest.dmn.app.port}")
    private Integer restDmnAppPort;

    @Value("${admin.rest.dmn.app.contextroot}")
    private String restDmnAppContextRoot;

    @Value("${admin.rest.dmn.app.restroot}")
    private String restDmnAppRestRoot;

    /**
     * FORM
     */
    @Value("${admin.rest.form.app.name}")
    private String restFormAppName;

    @Value("${admin.rest.form.app.description}")
    private String restFormAppDescription;

    @Value("${admin.rest.form.app.host}")
    private String restFormAppHost;

    @Value("${admin.rest.form.app.port}")
    private Integer restFormAppPort;

    @Value("${admin.rest.form.app.contextroot}")
    private String restFormAppContextRoot;

    @Value("${admin.rest.form.app.restroot}")
    private String restFormAppRestRoot;

    /**
     * CONTENT
     */
    @Value("${admin.rest.content.app.name}")
    private String restContentAppName;

    @Value("${admin.rest.content.app.description}")
    private String restContentAppDescription;

    @Value("${admin.rest.content.app.host}")
    private String restContentAppHost;

    @Value("${admin.rest.content.app.port}")
    private Integer restContentAppPort;

    @Value("${admin.rest.content.app.contextroot}")
    private String restContentAppContextRoot;

    @Value("${admin.rest.content.app.restroot}")
    private String restContentAppRestRoot;

    @Autowired
    protected Environment environment;

    @Autowired
    protected ServerConfigRepository serverConfigRepository;

    @Transactional
    public void createDefaultServerConfigs() {
        List<ServerConfig> serverConfigs = getDefaultServerConfigs();
        for (ServerConfig serverConfig : serverConfigs) {
            save(serverConfig, true);
        }
    }

    @Transactional
    public ServerConfig findOne(String id) {
        return serverConfigRepository.get(id);
    }

    @Transactional
    public ServerConfig findOneByEndpointTypeCode(EndpointType endpointType) {
        List<ServerConfig> serverConfigs = serverConfigRepository.getByEndpointType(endpointType);

        if (serverConfigs == null) {
            throw new FlowableServiceException("No server config found");
        }

        if (serverConfigs.size() > 1) {
            throw new FlowableServiceException("Only one server config per endpoint type allowed");
        }

        return serverConfigs.get(0);
    }

    @Transactional
    public List<ServerConfigRepresentation> findAll() {
        return createServerConfigRepresentation(serverConfigRepository.getAll());
    }

    @Transactional
    public void save(ServerConfig serverConfig, boolean encryptPassword) {
        if (encryptPassword) {
            serverConfig.setPassword(encrypt(serverConfig.getPassword()));
        }
        serverConfigRepository.save(serverConfig);
    }

    public String getServerConfigDecryptedPassword(ServerConfig serverConfig) {
        return decrypt(serverConfig.getPassword());
    }

    protected List<ServerConfigRepresentation> createServerConfigRepresentation(List<ServerConfig> serverConfigs) {
        List<ServerConfigRepresentation> serversRepresentations = new ArrayList<>();
        for (ServerConfig serverConfig : serverConfigs) {
            serversRepresentations.add(createServerConfigRepresentation(serverConfig));
        }
        return serversRepresentations;
    }

    protected ServerConfigRepresentation createServerConfigRepresentation(ServerConfig serverConfig) {
        ServerConfigRepresentation serverRepresentation = new ServerConfigRepresentation();
        serverRepresentation.setId(serverConfig.getId());
        serverRepresentation.setName(serverConfig.getName());
        serverRepresentation.setDescription(serverConfig.getDescription());
        serverRepresentation.setServerAddress(serverConfig.getServerAddress());
        serverRepresentation.setServerPort(serverConfig.getPort());
        serverRepresentation.setContextRoot(serverConfig.getContextRoot());
        serverRepresentation.setRestRoot(serverConfig.getRestRoot());
        serverRepresentation.setUserName(serverConfig.getUserName());
        serverRepresentation.setEndpointType(serverConfig.getEndpointType());
        return serverRepresentation;
    }

    public ServerConfig getDefaultServerConfig(EndpointType endpointType) {

        ServerConfig serverConfig = new ServerConfig();

        switch (endpointType) {

            case PROCESS:
                serverConfig.setName(restProcessAppName);
                serverConfig.setDescription(restProcessAppDescription);
                serverConfig.setServerAddress(restProcessAppHost);
                serverConfig.setPort(restProcessAppPort);
                serverConfig.setContextRoot(restProcessAppContextRoot);
                serverConfig.setRestRoot(restProcessAppRestRoot);
                serverConfig.setEndpointType(endpointType.getEndpointCode());
                break;

            case DMN:
                serverConfig.setName(restDmnAppName);
                serverConfig.setDescription(restDmnAppDescription);
                serverConfig.setServerAddress(restDmnAppHost);
                serverConfig.setPort(restDmnAppPort);
                serverConfig.setContextRoot(restDmnAppContextRoot);
                serverConfig.setRestRoot(restDmnAppRestRoot);
                serverConfig.setEndpointType(endpointType.getEndpointCode());
                break;

            case FORM:
                serverConfig.setName(restFormAppName);
                serverConfig.setDescription(restFormAppDescription);
                serverConfig.setServerAddress(restFormAppHost);
                serverConfig.setPort(restDmnAppPort);
                serverConfig.setContextRoot(restFormAppContextRoot);
                serverConfig.setRestRoot(restFormAppRestRoot);
                serverConfig.setEndpointType(endpointType.getEndpointCode());
                break;

            case CONTENT:
                serverConfig.setName(restContentAppName);
                serverConfig.setDescription(restContentAppDescription);
                serverConfig.setServerAddress(restContentAppHost);
                serverConfig.setPort(restDmnAppPort);
                serverConfig.setContextRoot(restContentAppContextRoot);
                serverConfig.setRestRoot(restContentAppRestRoot);
                serverConfig.setEndpointType(endpointType.getEndpointCode());
                break;
        }

        return serverConfig;
    }

    public List<ServerConfig> getDefaultServerConfigs() {
        List<ServerConfig> serverConfigs = new ArrayList<>();

        serverConfigs.add(getDefaultServerConfig(EndpointType.PROCESS));
        serverConfigs.add(getDefaultServerConfig(EndpointType.DMN));
        serverConfigs.add(getDefaultServerConfig(EndpointType.FORM));
        serverConfigs.add(getDefaultServerConfig(EndpointType.CONTENT));

        return serverConfigs;
    }

    public String getRestProcessAppName() {
        return restProcessAppName;
    }

    public void setRestProcessAppName(String restProcessAppName) {
        this.restProcessAppName = restProcessAppName;
    }

    public String getRestProcessAppDescription() {
        return restProcessAppDescription;
    }

    public void setRestProcessAppDescription(String restProcessAppDescription) {
        this.restProcessAppDescription = restProcessAppDescription;
    }

    public String getRestProcessAppHost() {
        return restProcessAppHost;
    }

    public void setRestProcessAppHost(String restProcessAppHost) {
        this.restProcessAppHost = restProcessAppHost;
    }

    public Integer getRestProcessAppPort() {
        return restProcessAppPort;
    }

    public void setRestProcessAppPort(Integer restProcessAppPort) {
        this.restProcessAppPort = restProcessAppPort;
    }

    public String getRestProcessAppContextRoot() {
        return restProcessAppContextRoot;
    }

    public void setRestProcessAppContextRoot(String restProcessAppContextRoot) {
        this.restProcessAppContextRoot = restProcessAppContextRoot;
    }

    public String getRestProcessAppRestRoot() {
        return restProcessAppRestRoot;
    }

    public void setRestProcessAppRestRoot(String restProcessAppRestRoot) {
        this.restProcessAppRestRoot = restProcessAppRestRoot;
    }

    public String getRestDmnAppName() {
        return restDmnAppName;
    }

    public void setRestDmnAppName(String restDmnAppName) {
        this.restDmnAppName = restDmnAppName;
    }

    public String getRestDmnAppDescription() {
        return restDmnAppDescription;
    }

    public void setRestDmnAppDescription(String restDmnAppDescription) {
        this.restDmnAppDescription = restDmnAppDescription;
    }

    public String getRestDmnAppHost() {
        return restDmnAppHost;
    }

    public void setRestDmnAppHost(String restDmnAppHost) {
        this.restDmnAppHost = restDmnAppHost;
    }

    public Integer getRestDmnAppPort() {
        return restDmnAppPort;
    }

    public void setRestDmnAppPort(Integer restDmnAppPort) {
        this.restDmnAppPort = restDmnAppPort;
    }

    public String getRestDmnAppContextRoot() {
        return restDmnAppContextRoot;
    }

    public void setRestDmnAppContextRoot(String restDmnAppContextRoot) {
        this.restDmnAppContextRoot = restDmnAppContextRoot;
    }

    public String getRestDmnAppRestRoot() {
        return restDmnAppRestRoot;
    }

    public void setRestDmnAppRestRoot(String restDmnAppRestRoot) {
        this.restDmnAppRestRoot = restDmnAppRestRoot;
    }

    public String getRestFormAppName() {
        return restFormAppName;
    }

    public void setRestFormAppName(String restFormAppName) {
        this.restFormAppName = restFormAppName;
    }

    public String getRestFormAppDescription() {
        return restFormAppDescription;
    }

    public void setRestFormAppDescription(String restFormAppDescription) {
        this.restFormAppDescription = restFormAppDescription;
    }

    public String getRestFormAppHost() {
        return restFormAppHost;
    }

    public void setRestFormAppHost(String restFormAppHost) {
        this.restFormAppHost = restFormAppHost;
    }

    public Integer getRestFormAppPort() {
        return restFormAppPort;
    }

    public void setRestFormAppPort(Integer restFormAppPort) {
        this.restFormAppPort = restFormAppPort;
    }

    public String getRestFormAppContextRoot() {
        return restFormAppContextRoot;
    }

    public void setRestFormAppContextRoot(String restFormAppContextRoot) {
        this.restFormAppContextRoot = restFormAppContextRoot;
    }

    public String getRestFormAppRestRoot() {
        return restFormAppRestRoot;
    }

    public void setRestFormAppRestRoot(String restFormAppRestRoot) {
        this.restFormAppRestRoot = restFormAppRestRoot;
    }

    public String getRestContentAppName() {
        return restContentAppName;
    }

    public void setRestContentAppName(String restContentAppName) {
        this.restContentAppName = restContentAppName;
    }

    public String getRestContentAppDescription() {
        return restContentAppDescription;
    }

    public void setRestContentAppDescription(String restContentAppDescription) {
        this.restContentAppDescription = restContentAppDescription;
    }

    public String getRestContentAppHost() {
        return restContentAppHost;
    }

    public void setRestContentAppHost(String restContentAppHost) {
        this.restContentAppHost = restContentAppHost;
    }

    public Integer getRestContentAppPort() {
        return restContentAppPort;
    }

    public void setRestContentAppPort(Integer restContentAppPort) {
        this.restContentAppPort = restContentAppPort;
    }

    public String getRestContentAppContextRoot() {
        return restContentAppContextRoot;
    }

    public void setRestContentAppContextRoot(String restContentAppContextRoot) {
        this.restContentAppContextRoot = restContentAppContextRoot;
    }

    public String getRestContentAppRestRoot() {
        return restContentAppRestRoot;
    }

    public void setRestContentAppRestRoot(String restContentAppRestRoot) {
        this.restContentAppRestRoot = restContentAppRestRoot;
    }
}
