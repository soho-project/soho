package work.soho.chat.biz.test;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import work.soho.admin.common.security.service.SohoUserDetailsService;
import work.soho.admin.common.security.userdetails.SohoUserDetails;

/**
 * 测试用
 */
@Component
public class TestSohoUserDetailsService implements SohoUserDetailsService {
    @Override
    public SohoUserDetails loadUserByUsername(String username) {
        return null;
    }

    @Override
    public String getUserRoleName() {
        return null;
    }

    @Override
    public UserDetails loadUserByToken(String token) {
        return null;
    }
}
