package work.soho.test.security.support;

import org.springframework.stereotype.Service;
import work.soho.admin.common.security.service.SohoUserDetailsService;
import work.soho.admin.common.security.userdetails.SohoUserDetails;

@Service
public class EmptySohoUserDetails implements SohoUserDetailsService {
    @Override
    public SohoUserDetails loadUserByUsername(String username) {
        return null;
    }

    @Override
    public String getUserRoleName() {
        return "empty";
    }
}
