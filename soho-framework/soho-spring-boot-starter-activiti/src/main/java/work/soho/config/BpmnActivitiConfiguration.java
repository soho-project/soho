package work.soho.config;

import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import work.soho.core.consts.WebFilterOrderEnum;
import work.soho.core.web.ActivitiWebFilter;

@Configuration
public class BpmnActivitiConfiguration {

    /**
     * Activiti bpmn img&xml generator.
     */
    @Bean
    public ProcessDiagramGenerator processDiagramGenerator() {
        return new DefaultProcessDiagramGenerator();
    }

    @Bean
    public FilterRegistrationBean<ActivitiWebFilter> activitiWebFilter() {
        FilterRegistrationBean<ActivitiWebFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ActivitiWebFilter());
        //此处后期须更改执行过滤顺序
        registrationBean.setOrder(WebFilterOrderEnum.ACTIVITI_FILTER);
        return registrationBean;
    }

    /**
     * ProcessEngineConfigurationConfigurer ,transaction & cap.
     */
    @Bean
    public ProcessEngineConfigurationConfigurer processEngineConfigurationConfigurer(
            PlatformTransactionManager platformTransactionManager) {
        return processEngineConfiguration -> processEngineConfiguration.setTransactionManager(platformTransactionManager);
    }


}
