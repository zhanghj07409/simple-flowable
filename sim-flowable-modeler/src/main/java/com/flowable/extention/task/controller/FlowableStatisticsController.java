package com.flowable.extention.task.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sim.common.vo.FlowableTasksVo;
import org.flowable.app.model.common.RemoteGroup;
import org.flowable.app.model.common.RemoteUser;
import org.flowable.app.security.SecurityUtils;
import org.flowable.app.service.exception.NotFoundException;
import com.flowable.extention.task.query.TaskInfoQuery;
import com.flowable.extention.task.service.FlowableStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
@RestController
@RequestMapping("/workflow")
public class FlowableStatisticsController {

    @Autowired
    private FlowableStatisticsService flowableStatisticsService;

    /**
     *
     * @param userId
     * @param keyName
     * @param processDefinitionKey
     * @return List<Map<String,Object>>
     *     Map key 包括
     *          total 记录数
     *          name
     */
    @RequestMapping("/list/statistics.do")
    public JSONObject listStatistics(@RequestParam(value = "userId",required = false) String userId,
                                        @RequestParam("keyName")String keyName,
                                        @RequestParam("processDefinitionKey")String processDefinitionKey){
        if(!StringUtils.hasText(userId)){
            userId = SecurityUtils.getCurrentUserId();
        }
        RemoteUser remoteUser = SecurityUtils.getRemoteUser(userId);
        if(remoteUser == null){
            throw new NotFoundException("current user is not found");
        }
        List<String> groupIds = new ArrayList<>();
        for(RemoteGroup remoteGroup : remoteUser.getGroups()){
            groupIds.add(remoteGroup.getId());
        }
        List<Map<String,Object>> list = flowableStatisticsService.listStatistics(userId,groupIds,keyName,processDefinitionKey);

        JSONArray jsonArray = (JSONArray)JSONObject.toJSON(list);
        JSONObject json =  new JSONObject();
        json.put("rows",jsonArray);
        return json;
    }

    @RequestMapping(value = "/statistics/listdetail.do",method = {RequestMethod.GET,RequestMethod.POST})
    public JSONObject statisticsListDetail(FlowableTasksVo flowableTasksVo){
        return flowableStatisticsService.statisticsListDetail(flowableTasksVo);
    }

    @RequestMapping(value = "/statistics/detail.do",method = {RequestMethod.GET,RequestMethod.POST})
    public Map<String,TaskInfoQuery> statisticsDetail(@RequestBody FlowableTasksVo flowableTasksVo){
        return flowableStatisticsService.listStatisticsDetail(flowableTasksVo);
    }

    @RequestMapping(value = "/select/task/by/operation-flag.do",method = {RequestMethod.GET,RequestMethod.POST})
    public Map<String,TaskInfoQuery> selectTaskByOperationFlag(FlowableTasksVo flowableTasksVo){
        return flowableStatisticsService.selectTaskByOperationFlag(flowableTasksVo);
    }

    @RequestMapping(value = "/get/process-instance/by/business-key.do",method = RequestMethod.GET)
    public String getProcessInstanceIdByBusinessKey(@RequestParam("businessKey") String businessKey){
        return flowableStatisticsService.getProcessInstanceIdByBusinessKey(businessKey);
    }

    @RequestMapping(value = "/get/process-instanceobj/by/business-key.do",method = RequestMethod.GET)
    public JSONObject getProcessInstanceByBusinessKey(@RequestParam("businessKey") String businessKey){
        return flowableStatisticsService.getProcessInstanceByBusinessKey(businessKey);
    }

}
