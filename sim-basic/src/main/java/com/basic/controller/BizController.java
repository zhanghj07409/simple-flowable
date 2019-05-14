package com.basic.controller;

import com.sim.common.feign.WorkflowFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
@RestController
@RequestMapping("/biz")
public class BizController {

    @Autowired
    private WorkflowFeignClient workflowFeignClient;

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public void send(HttpServletRequest request) {
        Map<String,Object> map = new HashMap<String,Object>();

        String id = request.getParameter("id");
        String name = request.getParameter("name");

        map.put("$_USER_ID", "admin");
        map.put("name", name);
        map.put("id", id);

        workflowFeignClient.startProcessInstanceByKey("qingjia",id,map);
    }
}
