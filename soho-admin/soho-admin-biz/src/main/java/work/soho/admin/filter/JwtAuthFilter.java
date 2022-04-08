package work.soho.admin.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import work.soho.admin.service.impl.TokenServiceImpl;
import work.soho.admin.service.impl.UserDetailsServiceImpl;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final TokenServiceImpl tokenService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        UserDetailsServiceImpl.UserDetailsImpl loginUser = tokenService.getLoginUser(request);
        if (loginUser!=null && (SecurityContextHolder.getContext().getAuthentication() == null)) {
            RememberMeAuthenticationToken  rememberMeAuthenticationToken = new RememberMeAuthenticationToken(tokenService.getToken(request)
                    , loginUser, AuthorityUtils.createAuthorityList("admin"));

            rememberMeAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(rememberMeAuthenticationToken);
        }

        filterChain.doFilter(request, response);
    }
}
