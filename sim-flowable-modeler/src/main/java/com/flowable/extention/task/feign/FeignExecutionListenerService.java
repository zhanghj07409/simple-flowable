package com.flowable.extention.task.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
@FeignClient("sim-basic")
public interface FeignExecutionListenerService {
    @RequestMapping(value="/workflow/execution/listener/after.do",method = RequestMethod.POST)
     Map executionListenerAfter(@RequestBody Map<String, Object> variables);
}
