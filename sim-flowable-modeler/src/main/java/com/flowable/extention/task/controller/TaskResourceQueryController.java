package com.flowable.extention.task.controller;

import org.flowable.app.model.common.RemoteGroup;
import org.flowable.app.model.common.RemoteUser;
import org.flowable.app.model.common.UserRepresentation;
import org.flowable.app.security.SecurityUtils;
import org.flowable.app.model.ResultListGridDataEntity;
import org.flowable.app.model.runtime.TaskRepresentation;
import org.apache.commons.lang3.StringUtils;
import org.flowable.cmmn.api.CmmnHistoryService;
import org.flowable.cmmn.api.CmmnRepositoryService;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.history.HistoricCaseInstance;
import org.flowable.cmmn.api.repository.CaseDefinition;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.idm.api.User;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.TaskInfoQueryWrapper;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
@Controller
@RequestMapping("/workflow")
public class TaskResourceQueryController {

    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    protected CmmnRepositoryService cmmnRepositoryService;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    protected CmmnRuntimeService cmmnRuntimeService;

    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected CmmnHistoryService cmmnHistoryService;

    @ResponseBody
    @RequestMapping(value = "/rest/query/variables.do")
    public Map listVariable(@RequestParam(value = "taskId")String taskId){
        Map map = taskService.getVariables(taskId);
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/rest/query/tasks.do")
    public ResultListGridDataEntity listTasks(@RequestParam(value = "processDefinitionKey",required = false)String processDefinitionKey,
                                              @RequestParam(value = "page",required = false)String page,
                                              @RequestParam(value = "rows",required = false)String rows,
                                              @RequestParam(value = "isNeedListPage",required = false)String isNeedListPage) {
        if(!org.springframework.util.StringUtils.hasText(page)){
            page = "1";
        }
        if(!org.springframework.util.StringUtils.hasText(rows)){
            rows = "50";
        }
        return this.listTasks(processDefinitionKey,Integer.valueOf(page),Integer.valueOf(rows),isNeedListPage != null?true:false);
    }

    public ResultListGridDataEntity listTasks(String processDefinitionKey,int page,int pageSize,boolean isNeedListPage) {
        TaskQuery taskQuery = taskService.createTaskQuery();
        String userId = SecurityUtils.getCurrentUserId();
        User currentUser = SecurityUtils.getCurrentUserObject();
        if(org.springframework.util.StringUtils.hasText(processDefinitionKey)){
            taskQuery.processDefinitionKey(processDefinitionKey);
        }
        List<String> groupIds = new ArrayList<>();
        if(currentUser instanceof RemoteUser){
            RemoteUser remoteUser = (RemoteUser)currentUser;
            for(RemoteGroup group : remoteUser.getGroups()){
                groupIds.add(group.getId());
            }
        }
        if(groupIds.size() > 0){
            taskQuery.or().taskCandidateOrAssigned(userId).taskCandidateGroupIn(groupIds).endOr().active();
        }else{
            taskQuery.taskCandidateOrAssigned(userId).active();
        }
        TaskInfoQueryWrapper taskInfoQueryWrapper = new TaskInfoQueryWrapper(taskQuery);
        List<? extends TaskInfo> tasks = null;
        if(isNeedListPage) {
            tasks = taskInfoQueryWrapper.getTaskInfoQuery().listPage((page - 1) * pageSize, pageSize);
        }else {
            tasks = taskInfoQueryWrapper.getTaskInfoQuery().list();
        }
        Map<String, String> processInstancesNames = new HashMap<>();
        Map<String, String> caseInstancesNames = new HashMap<>();
        handleIncludeProcessInstance(taskInfoQueryWrapper, true, tasks, processInstancesNames);
        handleIncludeCaseInstance(taskInfoQueryWrapper, true, tasks, caseInstancesNames);

        ResultListGridDataEntity result = new ResultListGridDataEntity(convertTaskInfoList(tasks, processInstancesNames, caseInstancesNames));

        Long totalCount = taskInfoQueryWrapper.getTaskInfoQuery().count();
        result.setTotal(Long.valueOf(totalCount.intValue()));
        result.setPage(page);
        result.setPageCount(pageSize);

        return result;
    }

    /**
     * 根据工作流任务id获取对应业务主键
     * @param taskId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/rest/query/getBusinessKey.do")
    public Map getBusinessKey(@RequestParam(value = "taskId")String taskId){
       Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        String businessKey = processInstance.getBusinessKey();
        Map map = new HashMap();
        map.put("businessKey",businessKey);
        return map;
    }

    protected void handleIncludeProcessInstance(TaskInfoQueryWrapper taskInfoQueryWrapper, boolean includeProcessInstanceNode, List<? extends TaskInfo> tasks, Map<String, String> processInstanceNames) {
        if (includeProcessInstanceNode && CollectionUtils.isNotEmpty(tasks)) {
            Set<String> processInstanceIds = new HashSet<>();
            for (TaskInfo task : tasks) {
                if (task.getProcessInstanceId() != null) {
                    processInstanceIds.add(task.getProcessInstanceId());
                }
            }
            if (CollectionUtils.isNotEmpty(processInstanceIds)) {
                if (taskInfoQueryWrapper.getTaskInfoQuery() instanceof HistoricTaskInstanceQuery) {
                    List<HistoricProcessInstance> processInstances = historyService.createHistoricProcessInstanceQuery().processInstanceIds(processInstanceIds).list();
                    for (HistoricProcessInstance processInstance : processInstances) {
                        processInstanceNames.put(processInstance.getId(), processInstance.getName());
                    }
                } else {
                    List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().processInstanceIds(processInstanceIds).list();
                    for (ProcessInstance processInstance : processInstances) {
                        processInstanceNames.put(processInstance.getId(), processInstance.getName());
                    }
                }
            }
        }
    }

    protected void handleIncludeCaseInstance(TaskInfoQueryWrapper taskInfoQueryWrapper, boolean includeProcessInstanceNode, List<? extends TaskInfo> tasks, Map<String, String> caseInstanceNames) {
        if (includeProcessInstanceNode && CollectionUtils.isNotEmpty(tasks)) {
            Set<String> caseInstanceIds = new HashSet<>();
            for (TaskInfo task : tasks) {
                if (task.getScopeId() != null) {
                    caseInstanceIds.add(task.getScopeId());
                }
            }
            if (CollectionUtils.isNotEmpty(caseInstanceIds)) {
                if (taskInfoQueryWrapper.getTaskInfoQuery() instanceof HistoricTaskInstanceQuery) {
                    List<HistoricCaseInstance> caseInstances = cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceIds(caseInstanceIds).list();
                    for (HistoricCaseInstance caseInstance : caseInstances) {
                        caseInstanceNames.put(caseInstance.getId(), caseInstance.getName());
                    }
                } else {
                    List<CaseInstance> caseInstances = cmmnRuntimeService.createCaseInstanceQuery().caseInstanceIds(caseInstanceIds).list();
                    for (CaseInstance caseInstance : caseInstances) {
                        caseInstanceNames.put(caseInstance.getId(), caseInstance.getName());
                    }
                }
            }
        }
    }
    protected List<TaskRepresentation> convertTaskInfoList(List<? extends TaskInfo> tasks, Map<String, String> processInstanceNames, Map<String, String> caseInstancesNames) {
        List<TaskRepresentation> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tasks)) {
            for (TaskInfo task : tasks) {

                TaskRepresentation remoteTaskEntity = null;
                if (task.getScopeDefinitionId() != null) {
                    CaseDefinition caseDefinition = cmmnRepositoryService.getCaseDefinition(task.getScopeDefinitionId());
                    remoteTaskEntity = new TaskRepresentation(task, caseDefinition, caseInstancesNames.get(task.getScopeId()));

                } else {
                    ProcessDefinitionEntity processDefinition = null;
                    if (task.getProcessDefinitionId() != null) {
                        processDefinition = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(task.getProcessDefinitionId());
                    }
                    remoteTaskEntity = new TaskRepresentation(task, processDefinition, processInstanceNames.get(task.getProcessInstanceId()));
                }

                if (StringUtils.isNotEmpty(task.getAssignee())) {
                    User user = SecurityUtils.getRemoteUser(task.getAssignee());
                    if(user != null) {
                        remoteTaskEntity.setAssignee(new UserRepresentation(user));
                    }
                }
                result.add(remoteTaskEntity);
            }
        }
        return result;
    }
}
