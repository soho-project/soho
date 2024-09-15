package work.soho.test.security.support;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.util.Assert;
import work.soho.common.security.userdetails.SohoUserDetails;

import java.util.ArrayList;
import java.util.List;

public class SohoWithMockUserSecurityContextFactory  implements WithSecurityContextFactory<SohoWithUser> {
    @Override
    public SecurityContext createSecurityContext(SohoWithUser withUser) {
        String username = withUser.username();
        Assert.notNull(username, () -> withUser + " cannot have null username on both username and value properties");
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(withUser.role()));

        SohoUserDetails principal = new SohoUserDetails();
        principal.setUsername(withUser.username());
        principal.setId(withUser.id());
        principal.setAuthorities(grantedAuthorities);

        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(),
                principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
