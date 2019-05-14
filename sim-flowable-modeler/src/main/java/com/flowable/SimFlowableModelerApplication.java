package com.flowable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowable.common.DateConvert;
import com.flowable.extention.task.servlet.AppTaskDispatcherServletConfiguration;
import org.flowable.admin.servlet.AdminAppDispatcherServletConfiguration;
import org.flowable.app.servlet.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@SpringBootApplication(exclude = {LiquibaseAutoConfiguration.class,org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class},//,org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class
		scanBasePackageClasses = {SimFlowableModelerApplication.class})
@EnableTransactionManagement
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = -1)
@EnableEurekaClient
@EnableScheduling
@EnableFeignClients(basePackages = {"com.flowable","org.flowable"})
@Configuration
@ComponentScan(basePackages={"com.flowable.configuration","com.flowable.extention"},
				excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION,classes = Controller.class)})
@ServletComponentScan({"com.flowable","org.flowable"})
public class SimFlowableModelerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimFlowableModelerApplication.class, args);
	}

	@Autowired
	private ObjectMapper objectMapper;

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

	/**
	 * 前端传后端时间格式的转换
	 * @return
	 */
	@Bean
	public Converter<String, Date> addNewConvert() {
		return new Converter<String, Date>() {
			@Override
			public Date convert(String source) {
				DateConvert sdf = new DateConvert();
				Date date = null;
				date = sdf.convert((String) source);
				return date;
			}
		};
	}

	/**
	 * 后端传前端时间格式的转换
	 * @return
	 */
	@Bean
	public HttpMessageConverters customConverters() {
		Collection<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
		MappingJackson2HttpMessageConverter bean = new MappingJackson2HttpMessageConverter();
		bean.setObjectMapper(objectMapper);
		messageConverters.add(bean);
		return new HttpMessageConverters(true, messageConverters);
	}



	@Bean("dispatcherServlet")
	public ServletRegistrationBean dispatcherServlet(){
		//注解扫描上下文
		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		//base package
		applicationContext.register(ApiDispatcherServletConfiguration.class);
		//通过构造函数指定dispatcherServlet的上下文
		DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);

		//用ServletRegistrationBean包装servlet
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(dispatcherServlet);
		registrationBean.setLoadOnStartup(1);
		//指定urlmapping
		registrationBean.addUrlMappings("/api/*");
		//指定name，如果不指定默认为dispatcherServlet
		registrationBean.setName("apiServlet");
		return registrationBean;
	}

	@Bean("appServlet")
	public ServletRegistrationBean appServlet(){
		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.register(AppDispatcherServletConfiguration.class, AdminAppDispatcherServletConfiguration.class);
		DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(dispatcherServlet);
		registrationBean.setLoadOnStartup(1);
		registrationBean.addUrlMappings("/app/*");
		registrationBean.setName("appServlet");
		return registrationBean;
	}

	@Bean("dispatcherTaskServlet")
	public ServletRegistrationBean dispatcherTaskServlet(){
		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.register(AppTaskDispatcherServletConfiguration.class);
		DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(dispatcherServlet);
		registrationBean.setLoadOnStartup(1);
		registrationBean.addUrlMappings("*.do");
		registrationBean.setName("dispatcherTaskServlet");
		return registrationBean;
	}

	@Bean("appTaskServlet")
	public ServletRegistrationBean appTaskServlet(){
		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.register(TaskDispatcherServletConfiguration.class);
		DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(dispatcherServlet);
		registrationBean.setLoadOnStartup(1);
		registrationBean.addUrlMappings("/app-api/*");
		registrationBean.setName("appTaskServlet");
		return registrationBean;
	}

	@Bean("processApiServlet")
	public ServletRegistrationBean processApiServlet(){
		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.register(ProcessDispatcherServletConfiguration.class);
		DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(dispatcherServlet);
		registrationBean.setLoadOnStartup(1);
		registrationBean.addUrlMappings("/process-api/*");
		registrationBean.setName("processApiServlet");
		return registrationBean;
	}

	@Bean("dmnApiServlet")
	public ServletRegistrationBean dmnApiServlet(){
		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.register(DmnDispatcherServletConfiguration.class);
		DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(dispatcherServlet);
		registrationBean.setLoadOnStartup(1);
		registrationBean.addUrlMappings("/dmn-api/*");
		registrationBean.setName("dmnApiServlet");
		return registrationBean;
	}

	@Bean("formApiServlet")
	public ServletRegistrationBean formApiServlet(){
		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.register(FormDispatcherServletConfiguration.class);
		DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(dispatcherServlet);
		registrationBean.setLoadOnStartup(1);
		registrationBean.addUrlMappings("/form-api/*");
		registrationBean.setName("formApiServlet");
		return registrationBean;
	}

	@Bean("contentApiServlet")
	public ServletRegistrationBean contentApiServlet(){
		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.register(ContentDispatcherServletConfiguration.class);
		DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(dispatcherServlet);
		registrationBean.setLoadOnStartup(1);
		registrationBean.addUrlMappings("/content-api/*");
		registrationBean.setName("contentApiServlet");
		return registrationBean;
	}
}
