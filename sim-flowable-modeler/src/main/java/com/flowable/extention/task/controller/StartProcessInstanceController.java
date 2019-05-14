package com.flowable.extention.task.controller;

import org.flowable.app.model.runtime.ProcessInstanceRepresentation;
import org.flowable.app.service.runtime.FlowableProcessInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
@Controller
@RequestMapping("/workflow")
public class StartProcessInstanceController {

    @Autowired
    private FlowableProcessInstanceService flowableProcessInstanceService;

    @ResponseBody
    @RequestMapping(value = "/start/processInstance/byKey.do",produces= MediaType.APPLICATION_JSON_UTF8_VALUE,consumes =MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map startProcessInstanceByKey(@RequestParam("processDefinitionKey") String processDefinitionKey,@RequestParam("businessKey") String businessKey,
                                         @RequestBody(required = false) Map map){
        ProcessInstanceRepresentation processInstanceRepresentation = flowableProcessInstanceService.startNewProcessInstanceByKey(processDefinitionKey,businessKey,map);
        Map<String,Object> retMap = new HashMap<>();
        retMap.put("processInstanceId",processInstanceRepresentation.getId());
        return retMap;
    }
}
