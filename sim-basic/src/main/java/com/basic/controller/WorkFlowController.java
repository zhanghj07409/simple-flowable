package com.basic.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
@RestController
@RequestMapping("/workflow")
public class WorkFlowController {

    /**
     * 用户表缓存
     * @return
     */
    @RequestMapping(value = "/getAllUser", method = RequestMethod.POST)
    public List<Map<String,String>> getAllUser(){
        List<Map<String,String>> list = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        map.put("userId","admin");
        map.put("userName","系统管理员");
        map.put("email","abc@qq.com");
        list.add(map);

        Map<String,String> mapjing = new HashMap<>();
        mapjing.put("userId","100001");
        mapjing.put("userName","A经办");
        mapjing.put("email","abc@qq.com");
        list.add(mapjing);

        Map<String,String> mapfu = new HashMap<>();
        mapfu.put("userId","100002");
        mapfu.put("userName","A复核");
        mapfu.put("email","abc@qq.com");
        list.add(mapfu);

        return list;
    }

    /**
     * 角色表缓存
     * 数据格式 List<Map<String,String>>
     *    map.put("roleCode",entity.getRoleCode());
     *    map.put("roleName",entity.getRoleName());
     * @return
     */
    @RequestMapping(value = "/getAllRoler", method = RequestMethod.POST)
    public List<Map<String,String>> getAllRoler(){
        List<Map<String,String>> list = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        map.put("roleCode","admin");
        map.put("roleName","系统管理员");
        list.add(map);

        Map<String,String> mapjing = new HashMap<>();
        mapjing.put("roleCode","101");
        mapjing.put("roleName","经办");
        list.add(mapjing);

        Map<String,String> mapfu = new HashMap<>();
        mapfu.put("roleCode","102");
        mapfu.put("roleName","复核");
        list.add(mapfu);

        return list;
    }
    /**
     * 角色用户表缓存
     * @return
     */
    @RequestMapping(value = "/getAllRolerUser", method = RequestMethod.POST)
    public Map<String,List<Map<String,String>>> getAllRolerUser(){
        Map<String,List<Map<String,String>>> map = new HashMap<>();

        Map<String,String> mapguan = new HashMap<>();
        mapguan.put("userId","admin");
        mapguan.put("roleCode","admin");
        List<Map<String,String>> listguan = new ArrayList<>();
        listguan.add(mapguan);
        map.put("admin",listguan);

        Map<String,String> mapjing = new HashMap<>();
        mapjing.put("userId","100001");
        mapjing.put("roleCode","101");
        List<Map<String,String>> listjing = new ArrayList<>();
        listjing.add(mapjing);
        map.put("101",listjing);

        Map<String,String> mapfu = new HashMap<>();
        mapfu.put("userId","100002");
        mapfu.put("roleCode","102");
        List<Map<String,String>> listfu = new ArrayList<>();
        listfu.add(mapfu);
        map.put("102",listjing);

        return map;
    }


}
