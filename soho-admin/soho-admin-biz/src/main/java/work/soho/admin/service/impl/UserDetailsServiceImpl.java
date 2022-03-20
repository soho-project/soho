package work.soho.admin.service.impl;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import work.soho.admin.domain.AdminUser;
import work.soho.admin.service.AdminUserService;

import java.util.Collection;

/**
 * 用户鉴权相关实现
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AdminUserService adminUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUser adminUser = adminUserService.getByLoginName(username);
        if(adminUser != null) {
            UserDetailsImpl userDetails = new UserDetailsImpl();
            assert adminUser != null;
            userDetails.setUsername(String.valueOf(adminUser.getPhone()));
            userDetails.setPassword(adminUser.getPassword());
            userDetails.setId(String.valueOf(adminUser.getId()));
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
        UserDetailsServiceImpl.UserDetailsImpl userDetails = (UserDetailsServiceImpl.UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return userDetails;
    }

    @Data
    public static class UserDetailsImpl implements UserDetails {
        private String id;
        private String password;
        private String username;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return null;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return username;
        }

        public String getId() {
            return id;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}
