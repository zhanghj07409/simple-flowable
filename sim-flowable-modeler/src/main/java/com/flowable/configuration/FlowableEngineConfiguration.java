/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flowable.configuration;

import com.flowable.extention.task.dao.HistoryActTaskDao;
import com.flowable.extention.task.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.flowable.cmmn.api.*;
import org.flowable.cmmn.spring.configurator.SpringCmmnEngineConfigurator;
import org.flowable.content.api.ContentEngineConfigurationApi;
import org.flowable.content.api.ContentService;
import org.flowable.content.spring.SpringContentEngineConfiguration;
import org.flowable.content.spring.configurator.SpringContentEngineConfigurator;
import org.flowable.dmn.api.DmnEngineConfigurationApi;
import org.flowable.dmn.api.DmnHistoryService;
import org.flowable.dmn.api.DmnRepositoryService;
import org.flowable.dmn.api.DmnRuleService;
import org.flowable.dmn.spring.SpringDmnEngineConfiguration;
import org.flowable.dmn.spring.configurator.SpringDmnEngineConfigurator;
import org.flowable.engine.*;
import org.flowable.engine.TaskService;
import org.flowable.engine.common.api.delegate.event.FlowableEventListener;
import org.flowable.engine.common.runtime.Clock;
import org.flowable.engine.impl.agenda.DebugFlowableEngineAgendaFactory;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.util.EngineServiceUtil;
import org.flowable.engine.runtime.ProcessDebugger;
import org.flowable.form.api.FormEngineConfigurationApi;
import org.flowable.form.api.FormRepositoryService;
import org.flowable.form.spring.configurator.SpringFormEngineConfigurator;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.IdmManagementService;
import org.flowable.idm.engine.IdmEngine;
import org.flowable.idm.spring.SpringIdmEngineConfiguration;
import org.flowable.job.service.impl.asyncexecutor.AsyncExecutor;
import org.flowable.job.service.impl.asyncexecutor.DefaultAsyncJobExecutor;
import org.flowable.spring.ProcessEngineFactoryBean;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class FlowableEngineConfiguration {
    
    @Autowired
    protected DataSource dataSource;

    @Autowired
    protected PlatformTransactionManager transactionManager;

    @Autowired
    protected ProcessDebugger processDebugger;

    @Autowired
    protected MailConfig mailConfig;

    @Bean(name = "processEngine")
    public ProcessEngineFactoryBean processEngineFactoryBean() {
        ProcessEngineFactoryBean factoryBean = new ProcessEngineFactoryBean();
        factoryBean.setProcessEngineConfiguration(processEngineConfiguration());
        return factoryBean;
    }
    
    public ProcessEngine processEngine() {
        try {
            return processEngineFactoryBean().getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Bean(name = "cmmnEngineConfiguration")
    public CmmnEngineConfigurationApi cmmnEngineConfiguration() {
        ProcessEngineConfiguration processEngineConfiguration = processEngine().getProcessEngineConfiguration();
        return EngineServiceUtil.getCmmnEngineConfiguration(processEngineConfiguration);
    }
    
    @Bean(name = "dmnEngineConfiguration")
    public DmnEngineConfigurationApi dmnEngineConfiguration() {
        ProcessEngineConfiguration processEngineConfiguration = processEngine().getProcessEngineConfiguration();
        return EngineServiceUtil.getDmnEngineConfiguration(processEngineConfiguration);
    }
    
    @Bean(name = "formEngineConfiguration")
    public FormEngineConfigurationApi formEngineConfiguration() {
        ProcessEngineConfiguration processEngineConfiguration = processEngine().getProcessEngineConfiguration();
        return EngineServiceUtil.getFormEngineConfiguration(processEngineConfiguration);
    }
    
    @Bean(name = "contentEngineConfiguration")
    public ContentEngineConfigurationApi contentEngineConfiguration() {
        ProcessEngineConfiguration processEngineConfiguration = processEngine().getProcessEngineConfiguration();
        return EngineServiceUtil.getContentEngineConfiguration(processEngineConfiguration);
    }

    @Bean(name = "processEngineConfiguration")
    public ProcessEngineConfigurationImpl processEngineConfiguration() {
        SpringProcessEngineConfiguration processEngineConfiguration = new SpringProcessEngineConfiguration();
        processEngineConfiguration.setDataSource(dataSource);
        processEngineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE);
        processEngineConfiguration.setTransactionManager(transactionManager);
        processEngineConfiguration.setAsyncExecutorActivate(true);
        processEngineConfiguration.setAsyncExecutor(asyncExecutor());

        //配置邮箱
        if(StringUtils.isNotEmpty(mailConfig.getMailServerHost())) {
            processEngineConfiguration.setMailServerHost(mailConfig.getMailServerHost());
            processEngineConfiguration.setMailServerPort(mailConfig.getMailServerPort()==null?25:mailConfig.getMailServerPort());
            processEngineConfiguration.setMailServerDefaultFrom(mailConfig.getMailServerDefaultFrom());
            processEngineConfiguration.setMailServerUsername(mailConfig.getMailServerUsername());
            processEngineConfiguration.setMailServerPassword(mailConfig.getMailServerPassword());
            processEngineConfiguration.setMailServerUseSSL(mailConfig.getMailServerUseSSL());
            processEngineConfiguration.setMailServerUseTLS(mailConfig.getMailServerUseTLS());
        }

        // Limit process definition cache
        processEngineConfiguration.setProcessDefinitionCacheLimit(128);

        // Enable safe XML. See http://www.flowable.org/docs/userguide/index.html#advanced.safe.bpmn.xml
        processEngineConfiguration.setEnableSafeBpmnXml(true);

        processEngineConfiguration.setDisableIdmEngine(true);
        processEngineConfiguration.addConfigurator(new SpringFormEngineConfigurator());
        
        SpringCmmnEngineConfigurator cmmnEngineConfigurator = new SpringCmmnEngineConfigurator();
        processEngineConfiguration.addConfigurator(cmmnEngineConfigurator);
        
        SpringDmnEngineConfiguration dmnEngineConfiguration = new SpringDmnEngineConfiguration();
        dmnEngineConfiguration.setHistoryEnabled(true);
        SpringDmnEngineConfigurator dmnEngineConfigurator = new SpringDmnEngineConfigurator();
        dmnEngineConfigurator.setDmnEngineConfiguration(dmnEngineConfiguration);
        processEngineConfiguration.addConfigurator(dmnEngineConfigurator);

        SpringContentEngineConfiguration contentEngineConfiguration = new SpringContentEngineConfiguration();

        SpringContentEngineConfigurator springContentEngineConfigurator = new SpringContentEngineConfigurator();
        springContentEngineConfigurator.setContentEngineConfiguration(contentEngineConfiguration);

        processEngineConfiguration.addConfigurator(springContentEngineConfigurator);

        //add customMybatisMappers
        Set<Class<?>> customMybatisMappers = new HashSet<>();
        customMybatisMappers.add(FlowableStatisticsDao.class);
        customMybatisMappers.add(HistoricProcessInstanceDao.class);
        customMybatisMappers.add(HistoryActTaskDao.class);
        processEngineConfiguration.setCustomMybatisMappers(customMybatisMappers);
        //add customMybatisXMLMappers
        Set<String> customMybatisXMLMappers = new HashSet<>();
        customMybatisXMLMappers.add("mapping/FlowableStatisticeMapper.xml");
        customMybatisXMLMappers.add("mapping/HistoricProcessInstanceMapper.xml");
        customMybatisXMLMappers.add("mapping/HistoryActTaskMapper.xml");
        processEngineConfiguration.setCustomMybatisXMLMappers(customMybatisXMLMappers);

        return processEngineConfiguration;
    }

    @Bean
    public FlowableEngineAgendaFactory agendaFactory() {
        DebugFlowableEngineAgendaFactory debugAgendaFactory = new DebugFlowableEngineAgendaFactory();
        debugAgendaFactory.setDebugger(processDebugger);
        return debugAgendaFactory;
    }

    @Bean
    public AsyncExecutor asyncExecutor() {
        DefaultAsyncJobExecutor asyncExecutor = new DefaultAsyncJobExecutor();
        asyncExecutor.setDefaultAsyncJobAcquireWaitTimeInMillis(5000);
        asyncExecutor.setDefaultTimerJobAcquireWaitTimeInMillis(5000);
        return asyncExecutor;
    }

    @Bean(name = "clock")
    @DependsOn("processEngine")
    public Clock getClock() {
        return processEngineConfiguration().getClock();
    }

    @Bean
    public RepositoryService repositoryService() {
        return processEngine().getRepositoryService();
    }

    @Bean
    public IdentityService identityService() {
        return processEngine().getIdentityService();
    }

    @Bean
    public RuntimeService runtimeService() {
        return processEngine().getRuntimeService();
    }

    @Bean(value="engineTaskService")
    public TaskService engineTaskService() {
        return processEngine().getTaskService();
    }

    @Bean
    public HistoryService historyService() {
        return processEngine().getHistoryService();
    }

    @Bean
    public FormService formService() {
        return processEngine().getFormService();
    }

    @Bean
    public ManagementService managementService() {
        return processEngine().getManagementService();
    }

    @Bean
    public FormRepositoryService formEngineRepositoryService() {
        return formEngineConfiguration().getFormRepositoryService();
    }

    @Bean
    public org.flowable.form.api.FormService formEngineFormService() {
        return formEngineConfiguration().getFormService();
    }

    @Bean
    public DmnRepositoryService dmnRepositoryService() {
        return dmnEngineConfiguration().getDmnRepositoryService();
    }

    @Bean
    public DmnRuleService dmnRuleService() {
        return dmnEngineConfiguration().getDmnRuleService();
    }
    
    @Bean
    public DmnHistoryService dmnHistoryService() {
        return dmnEngineConfiguration().getDmnHistoryService();
    }
    
    @Bean
    public CmmnRepositoryService cmmnRepositoryService() {
        return cmmnEngineConfiguration().getCmmnRepositoryService();
    }

    @Bean
    public CmmnRuntimeService cmmnRuntimeService() {
        return cmmnEngineConfiguration().getCmmnRuntimeService();
    }
    
    @Bean
    public CmmnTaskService cmmnTaskService() {
        return cmmnEngineConfiguration().getCmmnTaskService();
    }
    
    @Bean
    public CmmnHistoryService cmmnHistoryService() {
        return cmmnEngineConfiguration().getCmmnHistoryService();
    }

    @Bean
    public ContentService contentService() {
        return contentEngineConfiguration().getContentService();
    }

    @Bean
    public SpringIdmEngineConfiguration springIdmEngineConfiguration(){
        SpringIdmEngineConfiguration springIdmEngineConfiguration = new SpringIdmEngineConfiguration();
        springIdmEngineConfiguration.setDataSource(dataSource);
        springIdmEngineConfiguration.setDatabaseSchemaUpdate("true");
        springIdmEngineConfiguration.setTransactionManager(transactionManager);
        return springIdmEngineConfiguration;
    }

    @Bean
    public IdmEngine idmEngine(){
        IdmEngine idmEngine = springIdmEngineConfiguration().buildIdmEngine();
        return idmEngine;
    }

    @Bean
    public IdmIdentityService idmIdentityService(){
        IdmIdentityService idmIdentityService = springIdmEngineConfiguration().getIdmIdentityService();
        return idmIdentityService;
    }

    @Bean
    public IdmManagementService idmManagementService(){
        IdmManagementService idmManagementService = idmEngine().getIdmManagementService();
        return idmManagementService;
    }

}
