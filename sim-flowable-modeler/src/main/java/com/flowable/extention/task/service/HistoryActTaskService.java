package com.flowable.extention.task.service;

import com.flowable.extention.task.query.HistoryActTaskInstanceQuery;

import java.util.List;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
public interface HistoryActTaskService {

    Integer listHistoryActTaskInstanceCount(String procInstId, String userId);

    List<? extends HistoryActTaskInstanceQuery> listHistoryActTaskInstance(String procInstId, String userId, Integer page, Integer rows);

}
