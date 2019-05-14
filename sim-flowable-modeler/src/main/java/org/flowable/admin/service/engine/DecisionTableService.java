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
import org.flowable.admin.domain.ServerConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;

/**
 * Service for invoking Flowable REST services.
 */
@Service
public class DecisionTableService {

    @Autowired
    protected FlowableClientService clientUtil;

    public JsonNode listDecisionTables(ServerConfig serverConfig, Map<String, String[]> parameterMap) {
        URIBuilder builder = clientUtil.createUriBuilder("dmn-repository/decision-tables");

        for (String name : parameterMap.keySet()) {
            builder.addParameter(name, parameterMap.get(name)[0]);
        }
        HttpGet get = new HttpGet(clientUtil.getServerUrl(serverConfig, builder.toString()));
        get.setHeader("Cookie" , "SESSION=" + RequestContextHolder.getRequestAttributes().getSessionId());
        return clientUtil.executeRequest(get, serverConfig);
    }

    public JsonNode getDecisionTable(ServerConfig serverConfig, String decisionTableId) {
        HttpGet get = new HttpGet(clientUtil.getServerUrl(serverConfig, "dmn-repository/decision-tables/" + decisionTableId));
        get.setHeader("Cookie" , "SESSION=" + RequestContextHolder.getRequestAttributes().getSessionId());
        return clientUtil.executeRequest(get, serverConfig);
    }

    public JsonNode getEditorJsonForDecisionTable(ServerConfig serverConfig, String decisionTableId) {
        HttpGet get = new HttpGet(clientUtil.getServerUrl(serverConfig, "enterprise/decisions/decision-tables/" + decisionTableId + "/editorJson"));
        get.setHeader("Cookie" , "SESSION=" + RequestContextHolder.getRequestAttributes().getSessionId());
        return clientUtil.executeRequest(get, serverConfig);
    }

    public JsonNode getProcessDefinitionDecisionTables(ServerConfig serverConfig, String processDefinitionId) {
        HttpGet get = new HttpGet(clientUtil.getServerUrl(serverConfig, "repository/process-definitions/" + processDefinitionId + "/decision-tables"));
        get.setHeader("Cookie" , "SESSION=" + RequestContextHolder.getRequestAttributes().getSessionId());
        return clientUtil.executeRequest(get, serverConfig);
    }

}
