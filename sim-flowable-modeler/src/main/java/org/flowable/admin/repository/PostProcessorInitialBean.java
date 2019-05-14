package org.flowable.admin.repository;

import org.flowable.admin.domain.ServerConfig;
import org.flowable.admin.service.engine.ServerConfigService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: fej
 * @date: 2018/4/25
 * @description :
 * @version: 2.0
 */
@Component
@DependsOn("liquibase2")
public class PostProcessorInitialBean implements InitializingBean {

    @Autowired
    private ServerConfigService serverConfigService;

    @Autowired
    private ServerConfigRepository serverConfigRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("----------------------------------------------------配置参数到数据库START---");
        //serverConfigRepository.deleteAll();
        /*List<ServerConfig> list = serverConfigService.getDefaultServerConfigs();
        for(ServerConfig serverConfig : list){
            serverConfigRepository.save(serverConfig);
        }*/
        System.out.println("----------------------------------------------------配置参数到数据库END---");
    }
}
