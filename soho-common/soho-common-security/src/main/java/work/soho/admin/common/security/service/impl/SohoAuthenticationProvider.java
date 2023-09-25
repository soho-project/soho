package work.soho.admin.common.security.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import work.soho.admin.common.security.service.SohoUserDetailsService;
import work.soho.admin.common.security.userdetails.SohoUserDetails;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

@Component
public class SohoAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    @Autowired
    private Map<String, SohoUserDetailsService> sohoUserDetailsServiceMap;
    private PasswordEncoder passwordEncoder;

    public SohoAuthenticationProvider() {
        this.setPasswordEncoder(new BCryptPasswordEncoder());
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            this.logger.debug("Failed to authenticate since no credentials provided");
            throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        } else {
            String presentedPassword = authentication.getCredentials().toString();
            if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
                this.logger.debug("Failed to authenticate since password does not match stored value");
                throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
            }
        }
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        Collection<GrantedAuthority> grantedAuthorities = authentication.getAuthorities();
        SohoUserDetailsService sohoAuthService = getSohoUserDetailsByAuthentication(authentication);

        //检查是否匹配到服务
        if (sohoAuthService == null) {
            throw new InternalAuthenticationServiceException("UserDetailsService returned null, which is an interface contract violation");
        }

        SohoUserDetails sohoUserDetails = sohoAuthService.loadUserByUsername(authentication.getName());
        if (sohoUserDetails == null) {
            throw new InternalAuthenticationServiceException("UserDetailsService returned null, which is an interface contract violation");
        } else {
            return sohoUserDetails;
        }
    }

    /**
     * 获取用户数据源
     *
     * @param authentication
     * @return
     */
    private SohoUserDetailsService getSohoUserDetailsByAuthentication(UsernamePasswordAuthenticationToken authentication) {
        Iterator<Map.Entry<String, SohoUserDetailsService>> iterator = sohoUserDetailsServiceMap.entrySet().iterator();
        for(GrantedAuthority grantedAuthority:authentication.getAuthorities()) {
            //尝试不同角色获取用户
            while(iterator.hasNext()) {
                Map.Entry<String, SohoUserDetailsService> entry = iterator.next();
                System.out.println("----------------------------------");
                System.out.println(entry.getValue().getUserRoleName());
                if(entry.getValue().getUserRoleName().equals(grantedAuthority.getAuthority())) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }
}
