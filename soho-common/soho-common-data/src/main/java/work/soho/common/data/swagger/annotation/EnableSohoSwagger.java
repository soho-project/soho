package work.soho.common.data.swagger.annotation;

import org.springframework.context.annotation.Import;
import work.soho.common.data.swagger.SwaggerAutoConfiguration;

import java.lang.annotation.*;

/**
 * <p>
 * EnableSohoSwagger
 * </p>
 *
 * @author livk
 * @date 2022/1/25
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ SwaggerAutoConfiguration.class })
public @interface EnableSohoSwagger {

}
