package work.soho.common.security.service.impl;

import org.springframework.stereotype.Service;
import work.soho.common.security.service.SohoUserDetailsService;
import work.soho.common.security.userdetails.SohoUserDetails;

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
