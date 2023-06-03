package work.soho.admin.aspect;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import work.soho.admin.common.security.utils.SecurityUtils;
import work.soho.api.admin.annotation.Node;
import work.soho.admin.domain.AdminResource;
import work.soho.admin.service.AdminUserService;
import work.soho.common.core.result.R;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Aspect
@Component
@Configuration
public class SecurityAspect {

    @Autowired
    private AdminUserService adminUserService;

    @Around(value = "@annotation(work.soho.api.admin.annotation.Node)")
    public Object around(ProceedingJoinPoint invocation) throws Throwable {
        Node node = getNode(invocation);
        if(node != null) {
            //write node
            Long uid = SecurityUtils.getLoginUserId();
            if(uid != 1l) {
                //进行权限检查
                String key = node.value();
                HashMap<String, AdminResource> map = adminUserService.getResourceByUid(uid);
                if(map.get(key)==null) {
                    return R.error(2401, "未授权访问");
                }
            }
        }
        return invocation.proceed();
    }

    /**
     * get Log
     *
     * @param invocation
     * @return
     */
    private Node getNode(ProceedingJoinPoint invocation) {
        String methodName=invocation.getSignature().getName();
        Class<?> classTarget=invocation.getTarget().getClass();
        Class<?>[] par=((MethodSignature) invocation.getSignature()).getParameterTypes();
        try {
            Method objMethod=classTarget.getMethod(methodName, par);
            return objMethod.getAnnotation(Node.class);
        } catch (Exception e) {
            return null;
        }
    }
}
