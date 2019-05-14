package com.sim.common.service;

import java.util.Map;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
public interface WorkflowService {

    /**
     * 调用后置事件接口方法
     * @param data
     * @return 返回值将会存储到当前执行ID下的流程变量中去
     * @throws Exception
     */
    Map<String,Object> submitCallBack(Map<String, Object> data) throws Exception;
}
