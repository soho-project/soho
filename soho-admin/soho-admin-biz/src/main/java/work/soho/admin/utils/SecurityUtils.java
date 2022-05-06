package work.soho.admin.utils;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import work.soho.admin.service.impl.UserDetailsServiceImpl;

@UtilityClass
public class SecurityUtils {
    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    public UserDetailsServiceImpl.UserDetailsImpl getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsServiceImpl.UserDetailsImpl loginUser = (UserDetailsServiceImpl.UserDetailsImpl) authentication.getPrincipal();
        return loginUser;
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
