package work.soho.common.core.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * <p>
 * SpringContextHolder
 * </p>
 *
 * @author livk
 * @date 2021/11/2
 */
@Slf4j
@Component
@Lazy(false)
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {

	private static ApplicationContext applicationContext = null;

	@Override
	public void setApplicationContext(@Nullable ApplicationContext applicationContext) throws BeansException {
		if (SpringContextHolder.applicationContext != null) {
			log.warn("SpringContextHolder中的ApplicationContext被覆盖, 原有ApplicationContext为:"
					+ SpringContextHolder.applicationContext);
		}
		this.setSpringContext(applicationContext);
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public void destroy() {
		if (log.isDebugEnabled()) {
			log.debug("清除SpringContextHolder中的ApplicationContext:" + applicationContext);
		}
		this.setSpringContext(null);
	}

	private synchronized void setSpringContext(ApplicationContext applicationContext) {
		SpringContextHolder.applicationContext = applicationContext;
	}

	/**
	 * Spring事件发布
	 * @param event 事件
	 */
	public static void publishEvent(ApplicationEvent event) {
		applicationContext.publishEvent(event);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		return (T) applicationContext.getBean(name);
	}

	public static <T> T getBean(Class<T> typeClass) {
		return applicationContext.getBean(typeClass);
	}

	public static <T> T getBean(String name, Class<T> typeClass) {
		return applicationContext.getBean(name, typeClass);
	}

	public static String getProperty(String key, String defaultValue) {
		return applicationContext.getEnvironment().getProperty(key, defaultValue);
	}

	public static String getProperty(String key) {
		return getProperty(key, null);
	}

}
