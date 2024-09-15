package work.soho.admin.biz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import work.soho.common.security.service.SohoUserDetailsService;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.admin.biz.domain.AdminUser;
import work.soho.admin.biz.service.AdminUserService;

/**
 * 用户鉴权相关实现
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements SohoUserDetailsService {
    private final AdminUserService adminUserService;

    @Override
    public SohoUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUser adminUser = adminUserService.getByLoginName(username);
        if(adminUser != null) {
            SohoUserDetails userDetails = new SohoUserDetails();
            userDetails.setUsername(String.valueOf(adminUser.getPhone()));
            userDetails.setPassword(adminUser.getPassword());
            userDetails.setId(adminUser.getId());
            userDetails.setAuthorities(AuthorityUtils.createAuthorityList("admin"));
            return userDetails;
        }
        return null;
    }

    @Override
    public String getUserRoleName() {
        return "admin";
    }

    /**
     * 获取当前已登录的用户信息
     *
     * @return
     */
    public UserDetails getLoginUserDetails() {
        return (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
