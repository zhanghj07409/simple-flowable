package com.flowable.extention.task.util;

import java.util.Map;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
public class SuccessMessageUtils {

    private static ThreadLocal<Map<String,Object>> local = new ThreadLocal<Map<String,Object>>(){
        @Override
        protected Map<String,Object> initialValue() {
            return null;
        }
    };

    public static void set(Map<String,Object> map){
        local.set(map);
    }

    public static Map<String,Object> get(){
        Map<String,Object> map = local.get();
        remove();
        return map;
    }

    public static void remove(){
        local.remove();
    }
}
