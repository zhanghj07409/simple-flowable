package com.flowable.extention.task.dao;

import com.flowable.extention.task.query.TaskInfoQuery;
import com.flowable.extention.task.query.TaskInfoQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
public interface FlowableStatisticsDao {

    List<Map<String,Object>> listStatistics(@Param("userId") String userId, @Param("groupIds") List<String> groupIds, @Param("groupByKeyName") String groupByKeyName, @Param("processDefinitionKey") String processDefinitionKey);

    List<Map<String,Object>> listStatisticsBySubProcess(@Param("userId") String userId, @Param("groupIds") List<String> groupIds, @Param("groupByKeyName") String groupByKeyName, @Param("processDefinitionKeys") List<String> processDefinitionKeys);

    List<Map<String,Object>> listStatisticsByOperationFlag(@Param("userId") String userId, @Param("groupIds") List<String> groupIds, @Param("groupByKeyName") String groupByKeyName, @Param("processDefinitionKey") String processDefinitionKey,
                                                  @Param("operationKey") String operationKey, @Param("flags") String[] flags);

    List<Map<String,Object>> listStatisticsByOperationFlagAndSubProcess(@Param("userId") String userId, @Param("groupIds") List<String> groupIds, @Param("groupByKeyName") String groupByKeyName, @Param("processDefinitionKeys") List<String> processDefinitionKeys,
                                                           @Param("operationKey") String operationKey, @Param("flags") String[] flags);

    List<? extends TaskInfoQuery> selectTaskByoperationFlag(@Param("userId") String userId,
                                                            @Param("groupIds") List<String> groupIds,
                                                            @Param("keyName") String keyName,
                                                            @Param("keyValue") String keyValue,
                                                            @Param("processDefinitionKey") String processDefinitionKey,
                                                            @Param("operationKey") String operationKey,
                                                            @Param("flags") String[] flags,
                                                            @Param("offset") Integer offset,
                                                            @Param("rows") Integer rows);

    Integer selectTaskByoperationFlagCount(@Param("userId") String userId,
                                                            @Param("groupIds") List<String> groupIds,
                                                            @Param("keyName") String keyName,
                                                            @Param("keyValue") String keyValue,
                                                            @Param("processDefinitionKey") String processDefinitionKey,
                                                            @Param("operationKey") String operationKey,
                                                            @Param("flags") String[] flags);
}
