package com.sim.common.controller;

import com.sim.common.service.WorkflowService;
import com.sim.common.util.SpringUtil;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class CompleteTaskController {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CompleteTaskController.class);

    @RequestMapping("/execution/listener/after.do")
    public Map executionListenerAfter(@RequestBody Map<String, Object> variables) throws Exception {
        Long start =System.currentTimeMillis();
        String beanName = variables.get("beanName").toString();
        Map result = null;
        try {
            WorkflowService workflowService = SpringUtil.getBean(beanName, WorkflowService.class);
            result = workflowService.submitCallBack((Map<String, Object>) variables.get("variables"));
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception("执行后置事件异常！");
        }
        Long end =System.currentTimeMillis();
        log.info("耗时:"+(end-start));
        return result;
    }
}
