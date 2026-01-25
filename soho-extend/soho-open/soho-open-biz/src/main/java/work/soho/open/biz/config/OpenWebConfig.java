package work.soho.open.biz.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import work.soho.open.biz.component.BodyCachingFilter;

@Configuration
public class OpenWebConfig {

    @Bean
    public FilterRegistrationBean<BodyCachingFilter> bodyCachingFilterRegistration() {
        FilterRegistrationBean<BodyCachingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new BodyCachingFilter());
        registration.addUrlPatterns("/*");
        registration.setName("bodyCachingFilter");
        // Run before Spring Security's filter chain
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
