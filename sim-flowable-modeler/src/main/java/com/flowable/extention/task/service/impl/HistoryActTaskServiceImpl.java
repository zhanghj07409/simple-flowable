package com.flowable.extention.task.service.impl;

import com.flowable.extention.task.dao.HistoryActTaskDao;
import com.flowable.extention.task.query.HistoryActTaskInstanceQuery;
import com.flowable.extention.task.service.HistoryActTaskService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.common.impl.cmd.CustomSqlExecution;
import org.flowable.engine.impl.cmd.AbstractCustomSqlExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: zhanghj
 * 开发时间: 2017/4/11<br>
 * <br>
 */
@Service
public class HistoryActTaskServiceImpl implements HistoryActTaskService {

    @Autowired
    protected ManagementService managementService;

    @Override
    public List<? extends HistoryActTaskInstanceQuery> listHistoryActTaskInstance(String procInstId, String userId, Integer page, Integer rows) {
        CustomSqlExecution<HistoryActTaskDao, List<? extends HistoryActTaskInstanceQuery> > customSqlExecution =
                new AbstractCustomSqlExecution<HistoryActTaskDao, List<? extends HistoryActTaskInstanceQuery> >(HistoryActTaskDao.class) {
                    public List<? extends HistoryActTaskInstanceQuery> execute(HistoryActTaskDao customMapper) {
                        return customMapper.listHistoryActTaskInstance(procInstId,userId, page, rows);
                    }
                };
        return managementService.executeCustomSql(customSqlExecution);
    }

    public Integer listHistoryActTaskInstanceCount(String procInstId, String userId) {
        CustomSqlExecution<HistoryActTaskDao, Integer> customSqlExecution =
                new AbstractCustomSqlExecution<HistoryActTaskDao, Integer>(HistoryActTaskDao.class) {
                    public Integer execute(HistoryActTaskDao customMapper) {
                        return customMapper.listHistoryActTaskInstanceCount(procInstId,userId);
                    }
                };
        return managementService.executeCustomSql(customSqlExecution);
    }
}
