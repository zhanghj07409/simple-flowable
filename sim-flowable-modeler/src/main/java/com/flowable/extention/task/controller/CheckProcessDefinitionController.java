package com.flowable.extention.task.controller;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
@RestController
@RequestMapping("/workflow")
public class CheckProcessDefinitionController {

    @Autowired
    private RepositoryService repositoryService;

    @RequestMapping(value = "/check/process-definition.do", method = {RequestMethod.GET,RequestMethod.POST})
    public boolean checkProcessDefinition(@RequestParam("processDefinitionKey") String processDefinitionKey){
        ProcessDefinition list = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).latestVersion().singleResult();
        return list == null?false:true;
    }
}
