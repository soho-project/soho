package work.soho.common.data.swagger;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import work.soho.common.data.swagger.properties.SwaggerProperties;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * <p>
 * SwaggerAutoConfiguration
 * </p>
 *
 * @author livk
 * @date 2022/1/25
 */
@EnableOpenApi
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "swagger", name = "enable", havingValue = "true")
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerAutoConfiguration {

	/**
	 * 默认的排除路径，排除Spring Boot默认的错误处理路径和端点
	 */
	@Bean
	public Docket createRestApi(SwaggerProperties swaggerProperties) {
		ApiSelectorBuilder apis = new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo(swaggerProperties)).select()
				.apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()));
		ArrayList<String> excludePath = new ArrayList<>(swaggerProperties.getExcludePath());
		excludePath.addAll(Arrays.asList("/error", "/actuator/**"));
		apis.paths(PathSelectors.any());
		excludePath.forEach(path -> apis.paths(PathSelectors.ant(path).negate()));
		return apis.build().pathMapping("/");
	}

	private ApiInfo apiInfo(SwaggerProperties swaggerProperties) {
		return new ApiInfoBuilder().title(swaggerProperties.getTitle()).description(swaggerProperties.getDescription())
				.license(swaggerProperties.getLicense()).licenseUrl(swaggerProperties.getLicenseUrl())
				.termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl())
				.contact(new Contact(swaggerProperties.getContact().getName(), swaggerProperties.getContact().getUrl(),
						swaggerProperties.getContact().getEmail()))
				.version(swaggerProperties.getVersion()).build();
	}

}
