package com.flowable.extention.task.service.impl;

import com.flowable.extention.task.dao.HistoricProcessInstanceDao;
import com.flowable.extention.task.service.HistoricProcessInstanceService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.common.impl.cmd.CustomSqlExecution;
import org.flowable.engine.impl.cmd.AbstractCustomSqlExecution;
import org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntityImpl;
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
public class HistoricProcessInstanceServiceImpl implements HistoricProcessInstanceService {

    @Autowired
    protected ManagementService managementService;

    @Override
    public List<? extends HistoricProcessInstanceEntityImpl> listProcessInstanceByTaskAssignAndFinished(String userId, Integer page, Integer rows) {
        CustomSqlExecution<HistoricProcessInstanceDao, List<? extends HistoricProcessInstanceEntityImpl> > customSqlExecution =
            new AbstractCustomSqlExecution<HistoricProcessInstanceDao, List<? extends HistoricProcessInstanceEntityImpl> >(HistoricProcessInstanceDao.class) {
                public List<? extends HistoricProcessInstanceEntityImpl> execute(HistoricProcessInstanceDao customMapper) {
                    return customMapper.listProcessInstanceByTaskAssignAndFinished(userId,null, page, rows);
                }
            };
        return managementService.executeCustomSql(customSqlExecution);
    }

    @Override
    public List<? extends HistoricProcessInstanceEntityImpl> listProcessInstanceByBusinesskey(String businesskey, Integer page, Integer rows) {
        CustomSqlExecution<HistoricProcessInstanceDao, List<? extends HistoricProcessInstanceEntityImpl> > customSqlExecution =
                new AbstractCustomSqlExecution<HistoricProcessInstanceDao, List<? extends HistoricProcessInstanceEntityImpl> >(HistoricProcessInstanceDao.class) {
                    public List<? extends HistoricProcessInstanceEntityImpl> execute(HistoricProcessInstanceDao customMapper) {
                        return customMapper.listProcessInstanceByTaskAssignAndFinished(null,businesskey, page, rows);
                    }
                };
        return managementService.executeCustomSql(customSqlExecution);
    }

    @Override
    public Integer listProcessInstanceByTaskAssignCount(String userId) {
        CustomSqlExecution<HistoricProcessInstanceDao, Integer> customSqlExecution =
            new AbstractCustomSqlExecution<HistoricProcessInstanceDao, Integer>(HistoricProcessInstanceDao.class) {
                public Integer execute(HistoricProcessInstanceDao customMapper) {
                    return customMapper.listProcessInstanceByTaskAssignCount(userId);
                }
            };
        return managementService.executeCustomSql(customSqlExecution);
    }
}
