package com.flowable.extention.task.service;

import org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntityImpl;

import java.util.List;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
public interface HistoricProcessInstanceService {

    Integer listProcessInstanceByTaskAssignCount(String userId);
    List<? extends HistoricProcessInstanceEntityImpl> listProcessInstanceByTaskAssignAndFinished(String userId, Integer page, Integer rows);

    List<? extends HistoricProcessInstanceEntityImpl> listProcessInstanceByBusinesskey(String businesskey, Integer page, Integer rows);

}
