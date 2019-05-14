package com.flowable.extention.task.dao;

import org.apache.ibatis.annotations.Param;
import org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntityImpl;

import java.util.List;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
public interface HistoricProcessInstanceDao {

    /**
     * 参数为必传项
     * @param userId
     * @return
     */
    Integer listProcessInstanceByTaskAssignCount(@Param("userId") String userId);

    /**
     * userId 为必传项
     * @param userId
     * @return
     */
    List<? extends HistoricProcessInstanceEntityImpl> listProcessInstanceByTaskAssignAndFinished(@Param("userId") String userId, @Param("businesskey") String businesskey, @Param("page") Integer page, @Param("rows") Integer rows);
}
