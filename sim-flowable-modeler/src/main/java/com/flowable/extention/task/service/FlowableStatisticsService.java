package com.flowable.extention.task.service;

import com.alibaba.fastjson.JSONObject;
import com.flowable.extention.task.query.TaskInfoQuery;
import com.sim.common.vo.FlowableTasksVo;

import java.util.List;
import java.util.Map;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
public interface FlowableStatisticsService {

    List<Map<String,Object>> listStatistics(String userId, List<String> groupIds, String groupByKeyName, String processDefinitionKey);

    List<Map<String,Object>> listStatisticsBySubProcess(String userId, List<String> groupIds, String groupByKeyName, String processDefinitionKey);

    /**
     * 再根据操作标识添加数据
     * @param userId
     * @param groupIds
     * @param groupByKeyName
     * @param processDefinitionKey
     * @param lastOperationFlag
     * @return
     */
    List<Map<String,Object>> listStatisticsByOperationFlag(String userId, List<String> groupIds, String groupByKeyName, String processDefinitionKey,String operationKey,String lastOperationFlag);

    List<Map<String,Object>> listStatisticsByOperationFlagAndSubProcess(String userId, List<String> groupIds, String groupByKeyName, String processDefinitionKey,String operationKey,String lastOperationFlag);

    JSONObject statisticsListDetail(FlowableTasksVo flowableTasksVo);

    Map<String,TaskInfoQuery>  listStatisticsDetail(FlowableTasksVo flowableTasksVo);

    Map<String,TaskInfoQuery> selectTaskByOperationFlag(FlowableTasksVo flowableTasksVo);

    String getBusinessKey(String taskId);

    String getProcessInstanceIdByBusinessKey(String businessKey);

    JSONObject getProcessInstanceByBusinessKey(String businessKey);
}
