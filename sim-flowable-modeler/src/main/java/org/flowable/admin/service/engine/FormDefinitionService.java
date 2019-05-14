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

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.flowable.admin.domain.ServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;

/**
 * Service for invoking Flowable REST services.
 */
@Service
public class FormDefinitionService {

    @Autowired
    protected FlowableClientService clientUtil;

    public JsonNode listForms(ServerConfig serverConfig, Map<String, String[]> parameterMap) {
        URIBuilder builder = clientUtil.createUriBuilder("form-repository/form-definitions");

        for (String name : parameterMap.keySet()) {
            builder.addParameter(name, parameterMap.get(name)[0]);
        }
        HttpGet get = new HttpGet(clientUtil.getServerUrl(serverConfig, builder.toString()));
        get.setHeader("Cookie" , "SESSION=" + RequestContextHolder.getRequestAttributes().getSessionId());
        return clientUtil.executeRequest(get, serverConfig);
    }

    public JsonNode getForm(ServerConfig serverConfig, String formId) {
        HttpGet get = new HttpGet(clientUtil.getServerUrl(serverConfig, "form-repository/form-definitions/" + formId));
        get.setHeader("Cookie" , "SESSION=" + RequestContextHolder.getRequestAttributes().getSessionId());
        return clientUtil.executeRequest(get, serverConfig);
    }

    public JsonNode getProcessDefinitionForms(ServerConfig serverConfig, String processDefinitionId) {
        HttpGet get = new HttpGet(clientUtil.getServerUrl(serverConfig, "repository/process-definitions/" + processDefinitionId + "/form-definitions"));
        get.setHeader("Cookie" , "SESSION=" + RequestContextHolder.getRequestAttributes().getSessionId());
        return clientUtil.executeRequest(get, serverConfig);
    }
}