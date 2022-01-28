package work.soho.common.data.excel;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import work.soho.common.data.excel.resolver.ExcelMethodArgumentResolver;

import java.util.List;

/**
 * <p>
 * ExcelAutoConfiguration
 * </p>
 *
 * @author livk
 * @date 2022/1/17
 */
@Configuration(proxyBeanMethods = false)
public class ExcelAutoConfiguration implements WebMvcConfigurer {

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new ExcelMethodArgumentResolver());
	}

}
