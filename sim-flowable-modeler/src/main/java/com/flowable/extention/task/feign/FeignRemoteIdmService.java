package com.flowable.extention.task.feign;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
@FeignClient("sim-basic")
public interface FeignRemoteIdmService {

    @RequestMapping(value = "/workflow/getAllUser", method = RequestMethod.POST)
    public List<Map<String,String>> getAllUser();

    @RequestMapping(value = "/workflow/getAllRoler", method = RequestMethod.POST)
    public List<Map<String,String>> getAllRole();

    @RequestMapping(value = "/workflow/getAllRolerUser", method = RequestMethod.POST)
    public Map<String,List<Map<String,String>>> getAllRoleUser();

    /**
     * @Description: 获取单个组织信息,部分字段翻译成中文，不推荐使用
     * @Param: [orgId]
     * @return: com.alibaba.fastjson.JSONObject
     * @Author: zhangdl
     * @Date: 2018/4/4
     */
    @RequestMapping(value = "/organization/get", method = RequestMethod.POST)
    JSONObject get(@RequestParam(value = "orgId") String orgId);

    /**
     * 查询单个组织信息，字段未翻译
     * @return
     */
    @RequestMapping(value = "/organization/getByOrgId", method = RequestMethod.GET)
     JSONObject getByOrgId(@RequestParam(value = "orgId")String orgId);

}
