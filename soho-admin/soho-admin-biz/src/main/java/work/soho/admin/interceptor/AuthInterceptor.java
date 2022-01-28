package work.soho.admin.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //TODO         return HandlerInterceptor.super.preHandle(request, response, handler);
        System.out.println("this login function....");
        System.out.println(request.getRequestURI());
        return true;
    }
}
