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
package org.flowable.app.rest.runtime;

import org.flowable.app.model.ResultListGridDataEntity;
import org.flowable.app.model.common.UserRepresentation;
import org.flowable.app.model.runtime.TaskRepresentation;
import org.flowable.app.security.SecurityUtils;
import org.flowable.app.service.exception.BadRequestException;
import org.flowable.app.service.exception.NotPermittedException;
import org.flowable.app.service.runtime.PermissionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.engine.HistoryService;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.User;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class HistoricTaskQueryResource {

    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected IdmIdentityService idmIdentityService;

    @Autowired
    protected PermissionService permissionService;

    @RequestMapping(value = "/rest/query/history/tasks", method = RequestMethod.POST, produces = "application/json")
    public ResultListGridDataEntity listTasks(@RequestBody ObjectNode requestNode) {
        if (requestNode == null) {
            throw new BadRequestException("No request found");
        }

        HistoricTaskInstanceQuery taskQuery = historyService.createHistoricTaskInstanceQuery();

        User currentUser = SecurityUtils.getCurrentUserObject();

        JsonNode processInstanceIdNode = requestNode.get("processInstanceId");
        if (processInstanceIdNode != null && !processInstanceIdNode.isNull()) {
            String processInstanceId = processInstanceIdNode.asText();
            if (permissionService.hasReadPermissionOnProcessInstance(currentUser, processInstanceId)) {
                taskQuery.processInstanceId(processInstanceId);
            } else {
                throw new NotPermittedException();
            }
        }

        JsonNode finishedNode = requestNode.get("finished");
        if (finishedNode != null && !finishedNode.isNull()) {
            boolean isFinished = finishedNode.asBoolean();
            if (isFinished) {
                taskQuery.finished();
            } else {
                taskQuery.unfinished();
            }
        }

        List<HistoricTaskInstance> tasks = taskQuery.list();

        // get all users to have the user object available in the task on the client side
        ResultListGridDataEntity result = new ResultListGridDataEntity(convertTaskInfoList(tasks));
        return result;
    }

    protected List<TaskRepresentation> convertTaskInfoList(List<HistoricTaskInstance> tasks) {
        List<TaskRepresentation> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tasks)) {
            TaskRepresentation representation = null;
            for (HistoricTaskInstance task : tasks) {
                representation = new TaskRepresentation(task);

                User cachedUser = SecurityUtils.getRemoteUser(task.getAssignee());
                if (cachedUser != null) {
                    representation.setAssignee(new UserRepresentation(cachedUser));
                }

                result.add(representation);
            }
        }
        return result;
    }
}
