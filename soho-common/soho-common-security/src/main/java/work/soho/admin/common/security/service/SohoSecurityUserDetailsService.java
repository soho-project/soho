package work.soho.admin.common.security.service;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;

public interface SohoSecurityUserDetailsService extends UserDetailsService {

    @Override
    UserDetails loadUserByUsername(String username);
}
