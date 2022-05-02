package work.soho.admin.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import work.soho.admin.common.security.service.SohoSecurityUserDetailsService;
import work.soho.admin.domain.AdminUser;
import work.soho.admin.service.AdminUserService;

/**
 * 用户鉴权相关实现
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements SohoSecurityUserDetailsService {
    private final AdminUserService adminUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUser adminUser = adminUserService.getByLoginName(username);
        if(adminUser != null) {
            UserDetailsImpl userDetails = new UserDetailsImpl();
            userDetails.setUsername(String.valueOf(adminUser.getPhone()));
            userDetails.setPassword(adminUser.getPassword());
            userDetails.setId(adminUser.getId());
            return userDetails;
        }
        return null;
    }

    /**
     * 获取当前已登录的用户信息
     *
     * @return
     */
    public UserDetails getLoginUserDetails() {
        return (UserDetailsServiceImpl.UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
