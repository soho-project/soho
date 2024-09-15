package work.soho.common.security.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import work.soho.common.security.service.SohoSecurityUserDetailsService;
import work.soho.common.security.service.SohoUserDetailsService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SohoSecurityUserDetailsServiceImpl implements SohoSecurityUserDetailsService {
    private final Map<String, SohoUserDetailsService> sohoUserDetailsServiceList;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return null;
    }
}
