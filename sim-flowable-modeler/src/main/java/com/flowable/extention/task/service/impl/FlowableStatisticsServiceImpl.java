package com.flowable.extention.task.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sim.common.vo.FlowableTasksVo;
import org.flowable.app.domain.editor.Model;
import org.flowable.app.model.common.RemoteGroup;
import org.flowable.app.model.common.RemoteUser;
import org.flowable.app.repository.editor.ModelRepository;
import org.flowable.app.security.SecurityUtils;
import org.flowable.app.service.exception.BadRequestException;
import org.flowable.app.service.exception.InternalServerErrorException;
import com.flowable.extention.task.dao.FlowableStatisticsDao;
import com.flowable.extention.task.query.TaskInfoQuery;
import com.flowable.extention.task.service.FlowableStatisticsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flowable.cmmn.api.CmmnHistoryService;
import org.flowable.cmmn.api.CmmnRepositoryService;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.engine.*;
import org.flowable.engine.common.impl.cmd.CustomSqlExecution;
import org.flowable.engine.impl.cmd.AbstractCustomSqlExecution;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
@Service
public class FlowableStatisticsServiceImpl implements FlowableStatisticsService {

    public static final String RELATIONAL = "relational";

    @Autowired
    protected ManagementService managementService;

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected CmmnRuntimeService cmmnRuntimeService;

    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected CmmnHistoryService cmmnHistoryService;

    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    protected CmmnRepositoryService cmmnRepositoryService;

    @Autowired
    protected ModelRepository modelRepository;

    @Autowired
    protected ObjectMapper objectMapper;

    @Override
    public List<Map<String, Object>> listStatistics(final String userId, final List<String> groupIds, final String groupByKeyName, final String processDefinitionKey) {
        CustomSqlExecution<FlowableStatisticsDao, List<Map<String, Object>>> customSqlExecution =
                new AbstractCustomSqlExecution<FlowableStatisticsDao, List<Map<String,Object>>>(FlowableStatisticsDao.class) {

                    public List<Map<String, Object>> execute(FlowableStatisticsDao customMapper) {
                        return customMapper.listStatistics(userId,groupIds,groupByKeyName,processDefinitionKey);
                    }

                };
        return managementService.executeCustomSql(customSqlExecution);
    }

    @Override
    public List<Map<String, Object>> listStatisticsBySubProcess(String userId, List<String> groupIds, String groupByKeyName, String processDefinitionKey) {
        if(!StringUtils.hasText(processDefinitionKey)){
            throw new BadRequestException("processDefinitionKey " + processDefinitionKey + " is must needed");
        }
        List<Model> modelList = modelRepository.findByKeyAndType(processDefinitionKey, Model.MODEL_TYPE_BPMN);
        if(modelList == null || modelList.size() == 0){
            throw new InternalServerErrorException("Error reading processDefinition info " + processDefinitionKey);
        }
        Model model = Collections.max(modelList, new Comparator<Model>() {
            @Override
            public int compare(Model o1, Model o2) {
                return o1.getVersion() - o2.getVersion();
            }
        });
        List<String> processDefinitionKeys = new ArrayList<>();
        processDefinitionKeys.add(processDefinitionKey);

        CustomSqlExecution<FlowableStatisticsDao, List<Map<String, Object>>> customSqlExecution =
            new AbstractCustomSqlExecution<FlowableStatisticsDao, List<Map<String,Object>>>(FlowableStatisticsDao.class) {
                public List<Map<String, Object>> execute(FlowableStatisticsDao customMapper) {
                    return customMapper.listStatisticsBySubProcess(userId,groupIds,groupByKeyName,processDefinitionKeys);
                }
            };
        return managementService.executeCustomSql(customSqlExecution);
    }

    @Override
    public List<Map<String, Object>> listStatisticsByOperationFlag(String userId, List<String> groupIds, String groupByKeyName, String processDefinitionKey, String operationKey, String lastOperationFlag) {
        CustomSqlExecution<FlowableStatisticsDao, List<Map<String, Object>>> customSqlExecution =
                new AbstractCustomSqlExecution<FlowableStatisticsDao, List<Map<String,Object>>>(FlowableStatisticsDao.class) {

                    public List<Map<String, Object>> execute(FlowableStatisticsDao customMapper) {
                        return customMapper.listStatisticsByOperationFlag(userId,groupIds,groupByKeyName,processDefinitionKey,operationKey,lastOperationFlag.split(","));
                    }

                };
        return managementService.executeCustomSql(customSqlExecution);
    }

    @Override
    public List<Map<String, Object>> listStatisticsByOperationFlagAndSubProcess(String userId, List<String> groupIds, String groupByKeyName, String processDefinitionKey, String operationKey, String lastOperationFlag) {
        if(!StringUtils.hasText(processDefinitionKey)){
            throw new BadRequestException("processDefinitionKey " + processDefinitionKey + " is must needed");
        }
        List<Model> modelList = modelRepository.findByKeyAndType(processDefinitionKey, Model.MODEL_TYPE_BPMN);
        if(modelList == null || modelList.size() == 0){
            throw new InternalServerErrorException("Error reading processDefinition info " + processDefinitionKey);
        }
        Model model = Collections.max(modelList, new Comparator<Model>() {
            @Override
            public int compare(Model o1, Model o2) {
                return o1.getVersion() - o2.getVersion();
            }
        });
        List<String> processDefinitionKeys = new ArrayList<>();
        processDefinitionKeys.add(processDefinitionKey);

        CustomSqlExecution<FlowableStatisticsDao, List<Map<String, Object>>> customSqlExecution =
            new AbstractCustomSqlExecution<FlowableStatisticsDao, List<Map<String,Object>>>(FlowableStatisticsDao.class) {
                public List<Map<String, Object>> execute(FlowableStatisticsDao customMapper) {
                    return customMapper.listStatisticsByOperationFlagAndSubProcess(userId,groupIds,groupByKeyName,processDefinitionKeys,operationKey,lastOperationFlag.split(","));
                }
            };
        return managementService.executeCustomSql(customSqlExecution);
    }

    @Override
    public JSONObject statisticsListDetail(FlowableTasksVo flowableTasksVo) {
        if(!StringUtils.hasText(flowableTasksVo.getUserId())){
            flowableTasksVo.setUserId(SecurityUtils.getCurrentUserId());
        }
        List<String> groupIds = new ArrayList<>();
        RemoteUser remoteUser = SecurityUtils.getRemoteUser(flowableTasksVo.getUserId());
        if(remoteUser.getGroups() != null) {
            for (RemoteGroup group : remoteUser.getGroups()) {
                groupIds.add(group.getId());
            }
        }

        Integer page = flowableTasksVo.getPage();
        Integer rows = flowableTasksVo.getRows();
        if(page==null){
            page = 1;
        }
        if(rows==null){
            rows = 50;
        }
        Integer limitStart = (page - 1) * rows;

        final FlowableTasksVo finalFlowableTasksVo = flowableTasksVo;
        final List<String> finalGroupIds = groupIds;

        CustomSqlExecution<FlowableStatisticsDao, Integer> customSqlExecutionCount =
                new AbstractCustomSqlExecution<FlowableStatisticsDao, Integer>(FlowableStatisticsDao.class) {
                    public Integer execute(FlowableStatisticsDao customMapper) {
                        return customMapper.selectTaskByoperationFlagCount(finalFlowableTasksVo.getUserId(),finalGroupIds,finalFlowableTasksVo.getKeyName(),finalFlowableTasksVo.getKeyValue(),finalFlowableTasksVo.getProcessDefinitionKey(),
                                finalFlowableTasksVo.getOperationKey(),finalFlowableTasksVo.getOperationFlags()==null?null:finalFlowableTasksVo.getOperationFlags().split(","));
                    }
                };

        Long total = Long.valueOf(managementService.executeCustomSql(customSqlExecutionCount));

        CustomSqlExecution<FlowableStatisticsDao, List<? extends TaskInfoQuery>> customSqlExecution =
                new AbstractCustomSqlExecution<FlowableStatisticsDao, List<?extends TaskInfoQuery>>(FlowableStatisticsDao.class) {
                    public List<?extends TaskInfoQuery> execute(FlowableStatisticsDao customMapper) {
                        return customMapper.selectTaskByoperationFlag(finalFlowableTasksVo.getUserId(),finalGroupIds,finalFlowableTasksVo.getKeyName(),finalFlowableTasksVo.getKeyValue(),finalFlowableTasksVo.getProcessDefinitionKey(),
                                finalFlowableTasksVo.getOperationKey(),finalFlowableTasksVo.getOperationFlags()==null?null:finalFlowableTasksVo.getOperationFlags().split(","),
                                limitStart,finalFlowableTasksVo.getRows());
                    }
                };
        List<? extends TaskInfoQuery> tasks = managementService.executeCustomSql(customSqlExecution);

        JSONObject json =  new JSONObject();
        if(tasks != null && tasks.size() > 0){
            JSONArray jsonArray = new JSONArray();
            for(TaskInfoQuery task : tasks){
                JSONObject jsontask = (JSONObject) JSONObject.toJSON(task);
                if(task.getBusinessinfo()!=null){
                    JSONObject jsoninfo =  JSONObject.parseObject(task.getBusinessinfo());
                    jsontask.putAll(jsoninfo);
                }
                jsonArray.add(jsontask);
            }
            json.put("total",total);
            json.put("page",page);
            json.put("totalpage",total/rows+1);
            json.put("rows",jsonArray);
        }
        return json;
    }

    @Override
    public Map<String,TaskInfoQuery> listStatisticsDetail(FlowableTasksVo flowableTasksVo) {
        if(!StringUtils.hasText(flowableTasksVo.getUserId())){
            flowableTasksVo.setUserId(SecurityUtils.getCurrentUserId());
        }
        List<String> groupIds = new ArrayList<>();
        RemoteUser remoteUser = SecurityUtils.getRemoteUser(flowableTasksVo.getUserId());
        if(remoteUser.getGroups() != null) {
            for (RemoteGroup group : remoteUser.getGroups()) {
                groupIds.add(group.getId());
            }
        }
        if(flowableTasksVo.getPage() != null && flowableTasksVo.getRows() != null){
            flowableTasksVo.setPage((flowableTasksVo.getPage() - 1) * flowableTasksVo.getRows());
        }
        final FlowableTasksVo finalFlowableTasksVo = flowableTasksVo;
        final List<String> finalGroupIds = groupIds;
        CustomSqlExecution<FlowableStatisticsDao, List<? extends TaskInfoQuery>> customSqlExecution =
            new AbstractCustomSqlExecution<FlowableStatisticsDao, List<?extends TaskInfoQuery>>(FlowableStatisticsDao.class) {
                public List<?extends TaskInfoQuery> execute(FlowableStatisticsDao customMapper) {
                    return customMapper.selectTaskByoperationFlag(finalFlowableTasksVo.getUserId(),finalGroupIds,finalFlowableTasksVo.getKeyName(),finalFlowableTasksVo.getKeyValue(),finalFlowableTasksVo.getProcessDefinitionKey(),
                                                                    finalFlowableTasksVo.getOperationKey(),finalFlowableTasksVo.getOperationFlags()==null?null:finalFlowableTasksVo.getOperationFlags().split(","),
                                                                    finalFlowableTasksVo.getPage(),finalFlowableTasksVo.getRows());
                }
            };
        List<? extends TaskInfoQuery> tasks = managementService.executeCustomSql(customSqlExecution);
        if(tasks != null && tasks.size() > 0){
            Map<String,TaskInfoQuery> listTask = new HashMap<>();
            for(TaskInfoQuery task : tasks){
                listTask.put(task.getBusinessKey(),task);
            }
            return listTask;
        }
        return null;
    }

    @Override
    public Map<String,TaskInfoQuery> selectTaskByOperationFlag(FlowableTasksVo flowableTasksVo) {
        if(!StringUtils.hasText(flowableTasksVo.getUserId())){
            flowableTasksVo.setUserId(SecurityUtils.getCurrentUserId());
        }
        List<String> groupIds = new ArrayList<>();
        RemoteUser remoteUser = SecurityUtils.getRemoteUser(flowableTasksVo.getUserId());
        if(remoteUser.getGroups() != null) {
            for (RemoteGroup group : remoteUser.getGroups()) {
                groupIds.add(group.getId());
            }
        }
        if(flowableTasksVo.getPage() != null && flowableTasksVo.getRows() != null){
            flowableTasksVo.setPage((flowableTasksVo.getPage() - 1) * flowableTasksVo.getRows());
        }
        final FlowableTasksVo finalFlowableTasksVo = flowableTasksVo;
        final List<String> finalGroupIds = groupIds;
        CustomSqlExecution<FlowableStatisticsDao, List<? extends TaskInfoQuery>> customSqlExecution =
            new AbstractCustomSqlExecution<FlowableStatisticsDao, List<?extends TaskInfoQuery>>(FlowableStatisticsDao.class) {
                public List<?extends TaskInfoQuery> execute(FlowableStatisticsDao customMapper) {
                    return customMapper.selectTaskByoperationFlag(finalFlowableTasksVo.getUserId(),finalGroupIds,finalFlowableTasksVo.getKeyName(),finalFlowableTasksVo.getKeyValue(),finalFlowableTasksVo.getProcessDefinitionKey(),
                            finalFlowableTasksVo.getOperationKey(),finalFlowableTasksVo.getOperationFlags()==null?null:finalFlowableTasksVo.getOperationFlags().split(","),
                            finalFlowableTasksVo.getPage(),finalFlowableTasksVo.getRows());
                }
            };
        List<? extends TaskInfoQuery> tasks = managementService.executeCustomSql(customSqlExecution);
        if(tasks != null && tasks.size() > 0){
            Map<String,TaskInfoQuery> listTask = new HashMap<>();
            for(TaskInfoQuery task : tasks){
                listTask.put(task.getBusinessKey(),task);
            }
            return listTask;
        }
        return null;
    }

    @Override
    public String getBusinessKey(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        return processInstance.getBusinessKey();
    }

    @Override
    public String getProcessInstanceIdByBusinessKey(String businessKey) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
        if(processInstance != null){
            return processInstance.getProcessInstanceId();
        }
        return null;
    }

    @Override
    public JSONObject getProcessInstanceByBusinessKey(String businessKey) {
        ExecutionEntityImpl execution = (ExecutionEntityImpl) runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
        Object object =  execution.getOriginalPersistentState();
        JSONObject jsonobj  = (JSONObject)JSONObject.toJSON(object);
        return jsonobj;
    }
}
