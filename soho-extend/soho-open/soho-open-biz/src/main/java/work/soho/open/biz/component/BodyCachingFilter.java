package work.soho.open.biz.component;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class BodyCachingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            // 只处理 POST / PUT / PATCH
            String method = httpRequest.getMethod();
            if ("POST".equalsIgnoreCase(method)
                    || "PUT".equalsIgnoreCase(method)
                    || "PATCH".equalsIgnoreCase(method)) {

                BodyCachingHttpServletRequestWrapper wrapper =
                        new BodyCachingHttpServletRequestWrapper(httpRequest);

                chain.doFilter(wrapper, response);
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
