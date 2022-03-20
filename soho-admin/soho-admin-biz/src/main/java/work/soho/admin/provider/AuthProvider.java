package work.soho.admin.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.service.impl.TokenServiceImpl;
import work.soho.admin.service.impl.UserDetailsServiceImpl;
import work.soho.common.core.result.R;

import javax.annotation.Resource;

@RequiredArgsConstructor
@RestController
public class AuthProvider {
    private final TokenServiceImpl tokenService;
    @Resource
    private AuthenticationManager authenticationManager;

    @RequestMapping("/login")
    public Object login(String username, String password) {
        Authentication authentication = null;
        try{
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            //TODO 登录失败
            e.printStackTrace();
            return R.error("登录失败");
        }
        UserDetailsServiceImpl.UserDetailsImpl loginUser = (UserDetailsServiceImpl.UserDetailsImpl) authentication.getPrincipal();
        String token = tokenService.createToken(loginUser);
        return R.ok(token);
    }
}
