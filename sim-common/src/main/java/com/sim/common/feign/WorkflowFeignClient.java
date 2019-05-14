package com.sim.common.feign;

import com.alibaba.fastjson.JSONObject;
import com.sim.common.vo.FlowableTasksVo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
@FeignClient(name = "sim-modeler")
public interface WorkflowFeignClient {
    /**
     * 发起流程
     * @param processDefinitionKey  流程key
     * @param businessKey  业务主键
     * @param map  传入参数
     * @return
     */
    @RequestMapping(value = "/flowable-modeler/workflow/start/processInstance/byKey.do",produces= MediaType.APPLICATION_JSON_UTF8_VALUE,consumes =MediaType.APPLICATION_JSON_UTF8_VALUE,method = RequestMethod.POST)
    Map startProcessInstanceByKey(@RequestParam("processDefinitionKey") String processDefinitionKey, @RequestParam("businessKey") String businessKey,
                                  @RequestBody Map map);

    /**
     * 根据业务主键ID获取流程实例ID
     * @param businessKey
     * @return
     */
    @RequestMapping(value = "/flowable-modeler/workflow/get/process-instance/by/business-key.do",method = RequestMethod.GET)
    String getProcessInstanceIdByBusinessKey(@RequestParam("businessKey") String businessKey);


    /**
     * 根据业务主键ID获取流程实例信息
     * @param businessKey
     * @return
     */
    @RequestMapping(value = "/flowable-modeler/workflow/get/process-instanceobj/by/business-key.do",method = RequestMethod.GET)
    JSONObject getProcessInstanceByBusinessKey(@RequestParam("businessKey") String businessKey);

    /**
     * 查询待办任务
     * @param processDefinitionKey
     * @param page
     * @param rows
     * @param isNeedListPage
     * @return
     */
    @RequestMapping(value = "/flowable-modeler/rest/query/tasks.do")
    JSONObject listTasks(@RequestParam(value = "processDefinitionKey", required = false) String processDefinitionKey,
                         @RequestParam(value = "page", required = false) String page,
                         @RequestParam(value = "rows", required = false) String rows,
                         @RequestParam(value = "isNeedListPage", required = false) String isNeedListPage);


    /**
     * 查询待办任务详情和应付账款urid
     * @param flowableTasksVo
     * @return
     */
    @RequestMapping(value = "/flowable-modeler/workflow/statistics/detail.do",method = RequestMethod.GET)
    Map<String,Object> statisticsDetail(@RequestBody FlowableTasksVo flowableTasksVo);

    /**
     * 查询银行待办任务详情和应付账款urid
     * @param flowableTasksVo
     * @return
     */
    @RequestMapping(value = "/flowable-modeler/workflow/select/task/by/operation-flag.do",method = RequestMethod.GET)
    Map<String,Object> selectTaskByOperationFlag(@RequestParam(value = "flowableTasksVo") FlowableTasksVo flowableTasksVo);


    /**
     * 根据执行ID添加全局变量
     * @param executionId
     * @param variable
     */
    @RequestMapping(value = "/flowable-modeler/app-api/rest/process-instances/variables/add",method = RequestMethod.POST)
    void addVariablesByExecutionId(@RequestParam("executionId") String executionId, @RequestBody Map<String, Object> variable);


    /**
     * 校验流程是否存在
     * @param processDefinitionKey
     * @return
     */
    @RequestMapping(value = "/flowable-modeler/workflow/check/process-definition.do", method = RequestMethod.GET)
    boolean checkProcessDefinition(@RequestParam("processDefinitionKey") String processDefinitionKey);
}
