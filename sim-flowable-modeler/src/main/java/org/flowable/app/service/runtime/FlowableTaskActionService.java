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
package org.flowable.app.service.runtime;

import org.flowable.app.model.common.UserRepresentation;
import org.flowable.app.security.SecurityUtils;
import org.flowable.app.service.exception.BadRequestException;
import org.flowable.app.service.exception.NotFoundException;
import org.flowable.app.service.exception.NotPermittedException;
import org.flowable.app.model.runtime.TaskRepresentation;
import com.flowable.extention.task.util.SuccessMessageUtils;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.common.api.FlowableException;
import org.flowable.engine.task.Attachment;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.identitylink.service.IdentityLinkType;
import org.flowable.idm.api.User;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * @author Tijs Rademakers
 */
@Service
@Transactional
public class FlowableTaskActionService extends FlowableAbstractTaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowableTaskActionService.class);

    public void completeTask(String taskId, Map<String,Object> variables) {
        User currentUser = SecurityUtils.getCurrentUserObject();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        if (task == null) {
            throw new NotFoundException("Task with id: " + taskId + " does not exist");
        }

        if (!permissionService.isTaskOwnerOrAssignee(currentUser, task)) {
            if (StringUtils.isEmpty(task.getScopeType()) && !permissionService.validateIfUserIsInitiatorAndCanCompleteTask(currentUser, task)) {
                throw new NotPermittedException();
            }
        }

        try {
            if (StringUtils.isEmpty(task.getScopeType())) {
                if(variables != null){
                    taskService.complete(task.getId(),variables);
                }else {
                    taskService.complete(task.getId());
                }
            } else {
                if (variables != null) {
                    cmmnTaskService.complete(task.getId(), variables);
                } else {
                    cmmnTaskService.complete(task.getId());
                }
            }
            
        } catch (FlowableException e) {
            LOGGER.error("Error completing task {}", taskId, e);
            throw new BadRequestException("Task " + taskId + " can't be completed", e);
        }
    }

    /**
     * 上传附件
     * @param request
     * @param taskId
     * @return
     * @throws IOException
     */
    public List<Map<String,Object>> fileUpload(HttpServletRequest request, String taskId) throws IOException{
        TaskEntityImpl task = (TaskEntityImpl)taskService.createTaskQuery().taskId(taskId).singleResult();
        Map<String,MultipartFile> files = ((MultipartHttpServletRequest)request).getFileMap();
        List<Map<String,Object>> resultList = new ArrayList<>();
        if(files != null){
            for(String name : files.keySet()){
                MultipartFile multipartFile = files.get(name);
                Attachment attachment = taskService.createAttachment(multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().indexOf(".")),
                        taskId,task.getProcessInstanceId(),multipartFile.getName(),"",multipartFile.getInputStream());
                taskService.saveAttachment(attachment);
                Map<String,Object> stringObjectMap = new HashMap<>();
                stringObjectMap.put("attachmentId",attachment.getId());
                resultList.add(stringObjectMap);
            }
        }
        return resultList;
    }

    /**
     * 删除附件
     * @param attachmentId
     */
    public void deleteAttachment(String attachmentId){
        taskService.deleteAttachment(attachmentId);
    }

    /**
     * 接收并完成任务，同时返回执行成功的successCode和successMessage
     * @param taskId
     * @param transientVariables
     * @param extendVariableName
     * @param extendVariable
     * @param successList
     * @retrun
     */
    public Map<String,Object> claimAndCompleteTaskReturnSuccessMessage(String taskId, Map<String,Object> transientVariables, String extendVariableName, Map<String,Object> extendVariable,List<String> successList,String description) throws Exception{

        this.claimAndCompleteTask(taskId,transientVariables,extendVariableName,extendVariable,description);

        Map<String, Object> resultMap = SuccessMessageUtils.get();

        return resultMap;
    }

    /**
     * 接收并且完成任务
     * @param taskId
     * @param transientVariables
     * @param extendVariableName
     * @param extendVariable
     */
    public void claimAndCompleteTask(String taskId, Map<String,Object> transientVariables, String extendVariableName, Map<String,Object> extendVariable,String description) throws Exception{
        User currentUser = SecurityUtils.getCurrentUserObject();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        if (task == null) {
            throw new Exception("task为空");
        }
        if(task.getAssignee() == null) {
            permissionService.validateReadPermissionOnTask(currentUser, task.getId());
            try {
                taskService.claim(task.getId(), String.valueOf(currentUser.getId()));
            } catch (FlowableException e) {
                throw new BadRequestException("Task " + taskId + " can't be claimed", e);
            }
        }
        //设置全局扩展变量
        Map<String,Object> variables = new HashMap();
        if(extendVariable != null && !extendVariable.isEmpty()) {
            variables.put(extendVariableName,extendVariable);
        }

        TaskEntityImpl taskEntity = (TaskEntityImpl)taskService.createTaskQuery().taskId(taskId).singleResult();
        //保存意见
        taskEntity.setDescription(description==null?"":description);
        if(StringUtils.isNotBlank(description)){
            variables.put("description",description);
        }
        taskService.saveTask(taskEntity);
        //完成任务
        taskService.complete(task.getId(), variables, transientVariables);
    }

    public TaskRepresentation assignTask(String taskId, ObjectNode requestNode) {
        User currentUser = SecurityUtils.getCurrentUserObject();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        if (task == null) {
            throw new NotFoundException("Task with id: " + taskId + " does not exist");
        }

        checkTaskPermissions(taskId, currentUser, task);

        if (requestNode.get("assignee") != null) {

            // This method can only be called by someone in a tenant. Check if the user is part of the tenant
            String assigneeIdString = requestNode.get("assignee").asText();

            User cachedUser = SecurityUtils.getRemoteUser(assigneeIdString);
            if (cachedUser == null) {
                throw new BadRequestException("Invalid assignee id");
            }
            assignTask(currentUser, task, assigneeIdString);

        } else {
            throw new BadRequestException("Assignee is required");
        }

        task = taskService.createTaskQuery().taskId(taskId).singleResult();
        TaskRepresentation rep = new TaskRepresentation(task);
        fillPermissionInformation(rep, task, currentUser);

        populateAssignee(task, rep);
        rep.setInvolvedPeople(getInvolvedUsers(taskId));
        return rep;
    }

    public void involveUser(String taskId, ObjectNode requestNode) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        if (task == null) {
            throw new NotFoundException("Task with id: " + taskId + " does not exist");
        }

        User currentUser = SecurityUtils.getCurrentUserObject();
        permissionService.validateReadPermissionOnTask(currentUser, task.getId());

        if (requestNode.get("userId") != null) {
            String userId = requestNode.get("userId").asText();
            User user = SecurityUtils.getRemoteUser(userId);
            if (user == null) {
                throw new BadRequestException("Invalid user id");
            }
            taskService.addUserIdentityLink(taskId, userId, IdentityLinkType.PARTICIPANT);

        } else {
            throw new BadRequestException("User id is required");
        }

    }

    public void removeInvolvedUser(String taskId, ObjectNode requestNode) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        if (task == null) {
            throw new NotFoundException("Task with id: " + taskId + " does not exist");
        }

        permissionService.validateReadPermissionOnTask(SecurityUtils.getCurrentUserObject(), task.getId());

        String assigneeString = null;
        if (requestNode.get("userId") != null) {
            String userId = requestNode.get("userId").asText();
            if (SecurityUtils.getRemoteUser(userId) == null) {
                throw new BadRequestException("Invalid user id");
            }
            assigneeString = String.valueOf(userId);

        } else if (requestNode.get("email") != null) {

            String email = requestNode.get("email").asText();
            assigneeString = email;

        } else {
            throw new BadRequestException("User id or email is required");
        }

        taskService.deleteUserIdentityLink(taskId, assigneeString, IdentityLinkType.PARTICIPANT);
    }

    public void claimTask(String taskId) {

        User currentUser = SecurityUtils.getCurrentUserObject();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        if (task == null) {
            throw new NotFoundException("Task with id: " + taskId + " does not exist");
        }

        permissionService.validateReadPermissionOnTask(currentUser, task.getId());

        try {
            taskService.claim(task.getId(), String.valueOf(currentUser.getId()));
        } catch (FlowableException e) {
            throw new BadRequestException("Task " + taskId + " can't be claimed", e);
        }
    }

    protected void checkTaskPermissions(String taskId, User currentUser, Task task) {
        permissionService.validateReadPermissionOnTask(currentUser, task.getId());
    }

    protected String validateEmail(ObjectNode requestNode) {
        String email = requestNode.get("email") != null ? requestNode.get("email").asText() : null;
        if (email == null) {
            throw new BadRequestException("Email is mandatory");
        }
        return email;
    }

    protected void assignTask(User currentUser, Task task, String assigneeIdString) {
        try {
            String oldAssignee = task.getAssignee();
            taskService.setAssignee(task.getId(), assigneeIdString);

            // If the old assignee user wasn't part of the involved users yet, make it so
            addIdentiyLinkForUser(task, oldAssignee, IdentityLinkType.PARTICIPANT);

            // If the current user wasn't part of the involved users yet, make it so
            String currentUserIdString = String.valueOf(currentUser.getId());
            addIdentiyLinkForUser(task, currentUserIdString, IdentityLinkType.PARTICIPANT);

        } catch (FlowableException e) {
            throw new BadRequestException("Task " + task.getId() + " can't be assigned", e);
        }
    }

    protected void addIdentiyLinkForUser(Task task, String userId, String linkType) {
        List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(task.getId());
        boolean isOldUserInvolved = false;
        for (IdentityLink identityLink : identityLinks) {
            if (userId.equals(identityLink.getUserId()) && (identityLink.getType().equals(IdentityLinkType.PARTICIPANT) || identityLink.getType().equals(IdentityLinkType.CANDIDATE))) {
                isOldUserInvolved = true;
            }
        }
        if (!isOldUserInvolved) {
            taskService.addUserIdentityLink(task.getId(), userId, linkType);
        }
    }

    protected void populateAssignee(TaskInfo task, TaskRepresentation rep) {
        if (task.getAssignee() != null) {
            User cachedUser = SecurityUtils.getRemoteUser(task.getAssignee());
            if (cachedUser != null) {
                rep.setAssignee(new UserRepresentation(cachedUser));
            }
        }
    }

    protected List<UserRepresentation> getInvolvedUsers(String taskId) {
        List<HistoricIdentityLink> idLinks = historyService.getHistoricIdentityLinksForTask(taskId);
        List<UserRepresentation> result = new ArrayList<>(idLinks.size());

        for (HistoricIdentityLink link : idLinks) {
            // Only include users and non-assignee links
            if (link.getUserId() != null && !IdentityLinkType.ASSIGNEE.equals(link.getType())) {
                User cachedUser = SecurityUtils.getRemoteUser(link.getUserId());
                if (cachedUser != null) {
                    result.add(new UserRepresentation(cachedUser));
                }
            }
        }
        return result;
    }
}
