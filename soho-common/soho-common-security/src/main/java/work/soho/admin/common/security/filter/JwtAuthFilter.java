package work.soho.admin.common.security.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import work.soho.admin.common.security.service.impl.TokenServiceImpl;
import work.soho.admin.common.security.userdetails.SohoUserDetails;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final TokenServiceImpl tokenService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            SohoUserDetails loginUser = tokenService.getLoginUser(request);
            if (loginUser!=null && (SecurityContextHolder.getContext().getAuthentication() == null)) {
                RememberMeAuthenticationToken  rememberMeAuthenticationToken = new RememberMeAuthenticationToken(tokenService.getToken(request)
                        , loginUser, loginUser.getAuthorities());

                rememberMeAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(rememberMeAuthenticationToken);
            }
        } catch (Exception e) {
            log.error("parse token error: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
