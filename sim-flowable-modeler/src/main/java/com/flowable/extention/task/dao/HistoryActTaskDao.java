package com.flowable.extention.task.dao;

import com.flowable.extention.task.query.HistoryActTaskInstanceQuery;
import com.flowable.extention.task.query.HistoryActTaskInstanceQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
public interface HistoryActTaskDao {

    /**
     * 参数为必传项
     * @param procInstId
     * @return
     */
    Integer listHistoryActTaskInstanceCount(@Param("procInstId") String procInstId, @Param("userId") String userId);

    /**
     * userId 为必传项
     * @param procInstId
     * @return
     */
    List<? extends HistoryActTaskInstanceQuery> listHistoryActTaskInstance(@Param("procInstId") String procInstId, @Param("userId") String userId, @Param("page") Integer page, @Param("rows") Integer rows);
}
