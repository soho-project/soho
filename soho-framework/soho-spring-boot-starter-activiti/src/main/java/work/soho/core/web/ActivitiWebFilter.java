package work.soho.core.web;


import java.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;
import work.soho.core.util.ActivitiUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Activiti Web Filter
 *
 * @author cv&latke
 */
public class ActivitiWebFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            // 设置工作流的用户
            Long userId = 1L; //此处预设,应为当前系统ID.
            if (userId != null) {
                ActivitiUtils.setAuthenticatedUserId(userId);
            }
            chain.doFilter(request, response);
        } finally {
            ActivitiUtils.clearAuthenticatedUserId();
        }
    }

}
