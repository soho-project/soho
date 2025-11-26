package work.soho.common.security.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import work.soho.common.core.result.R;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.service.SohoRoleResourceService;
import work.soho.common.security.service.SohoRoleUserResourceService;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.common.security.utils.SecurityUtils;

import java.lang.reflect.Method;
import java.util.Optional;

@Log4j2
@Aspect
@Component
@Configuration
@RequiredArgsConstructor
@ConditionalOnBean(SohoRoleUserResourceService.class)
public class SecurityAspect {
    private final SohoRoleResourceService sohoRoleResourceService;

    @Around(value = "@annotation(work.soho.common.security.annotation.Node)")
    public Object around(ProceedingJoinPoint invocation) throws Throwable {
        Node node = getNode(invocation);
        if(node != null) {
            //write node
            Long uid = SecurityUtils.getLoginUserId();

            if(uid != 1L) {
                SohoUserDetails userDetails = SecurityUtils.getLoginUser();
                Optional<? extends GrantedAuthority> roleName = userDetails.getAuthorities().stream().findFirst();
                //进行权限检查
                String key = node.value();
                if(!sohoRoleResourceService.isExists(roleName.get().getAuthority(), uid, key)) {
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
