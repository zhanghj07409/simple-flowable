package com.flowable.extention.task.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
@Service("UserQueryService")
public class QueryUserServiceImpl {

    Logger log=LoggerFactory.getLogger(QueryUserServiceImpl.class);
    @Autowired
    private RestTemplate restTemplate;

    public List<String> getUserByOrgAndRole(String orgId, String roleId){
        return null;//feignQueryUserService.getUserByOrgAndRole(orgId, roleId);
    }

    private HttpHeaders getHeaders(){
        HttpHeaders headers = new HttpHeaders();
        List<String> contentType = new ArrayList<>();
        contentType.add(MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.put(HttpHeaders.CONTENT_TYPE,contentType);
        return  headers;
    }


}
