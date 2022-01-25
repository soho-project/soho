package work.soho.common.web.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>
 * RequestUtil
 * </p>
 *
 * @author livk
 * @date 2021/11/2
 */
@UtilityClass
public class RequestUtil {

	public HttpServletRequest getRequest() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
		Assert.notNull(servletRequestAttributes, "attributes not null!");
		return servletRequestAttributes.getRequest();
	}

	public HttpSession getSession() {
		return RequestUtil.getRequest().getSession();
	}

	public String getParameter(String name) {
		return RequestUtil.getRequest().getParameter(name);
	}

	public String getHeader(String headerName) {
		return RequestUtil.getRequest().getHeader(headerName);
	}

	public Map<String, String> getHeaders() {
		HttpServletRequest request = RequestUtil.getRequest();
		Map<String, String> map = new LinkedHashMap<>();
		Enumeration<String> enumeration = request.getHeaderNames();
		if (enumeration != null) {
			while (enumeration.hasMoreElements()) {
				String key = enumeration.nextElement();
				String value = request.getHeader(key);
				map.put(key, value);
			}
		}
		return map;
	}

}
