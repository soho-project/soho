package work.soho.admin.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import work.soho.admin.interceptor.AuthInterceptor;

@Configuration
@RequiredArgsConstructor
public class AuthConfiguration implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
       // registry.addInterceptor(authInterceptor).addPathPatterns("/**").excludePathPatterns("/login/**");
    }
}
