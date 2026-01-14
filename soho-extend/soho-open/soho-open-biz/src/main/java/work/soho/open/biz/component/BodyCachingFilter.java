package work.soho.open.biz.component;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Locale;

@Component
public class BodyCachingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            String method = httpRequest.getMethod();
            String contentType = httpRequest.getContentType();

            boolean isMultipart = contentType != null
                    && contentType.toLowerCase(Locale.ROOT).startsWith("multipart/");

            // 只对 非 multipart 的 POST / PUT / PATCH 做 body 缓存
            if (!isMultipart &&
                    ("POST".equalsIgnoreCase(method)
                            || "PUT".equalsIgnoreCase(method)
                            || "PATCH".equalsIgnoreCase(method))) {

                BodyCachingHttpServletRequestWrapper wrapper =
                        new BodyCachingHttpServletRequestWrapper(httpRequest);

                chain.doFilter(wrapper, response);
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
