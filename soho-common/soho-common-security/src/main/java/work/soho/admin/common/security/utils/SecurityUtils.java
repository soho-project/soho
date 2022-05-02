package work.soho.admin.common.security.utils;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import work.soho.admin.common.security.service.SohoSecurityUserDetailsService;

@UtilityClass
public class SecurityUtils {
    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    public SohoSecurityUserDetailsService.UserDetailsImpl getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (SohoSecurityUserDetailsService.UserDetailsImpl) authentication.getPrincipal();
    }

    /**
     * 获取当前登录的用户
     *
     * @return
     */
    public Long getLoginUserId() {
        return getLoginUser().getId();
    }
}
