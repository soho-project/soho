package work.soho.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.service.impl.TokenServiceImpl;
import work.soho.admin.service.impl.UserDetailsServiceImpl;
import work.soho.api.admin.vo.AdminUserLoginVo;
import work.soho.common.core.result.R;

import javax.annotation.Resource;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class AuthController {
    private final TokenServiceImpl tokenService;
    @Resource
    private AuthenticationManager authenticationManager;

    @PostMapping(value = "/login")
    public Object login(@RequestBody AdminUserLoginVo adminUserLoginVo) {
        Authentication authentication = null;
        try{
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(adminUserLoginVo.getUsername(), adminUserLoginVo.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("登录失败");
        }
        UserDetailsServiceImpl.UserDetailsImpl loginUser = (UserDetailsServiceImpl.UserDetailsImpl) authentication.getPrincipal();
        Map<String, String> token = tokenService.createTokenInfo(loginUser);
        return R.success(token);
    }

    /**
     * 用户登出
     *
     * @return
     */
    @GetMapping("/logout")
    public R<Boolean> logout() {
        //nothing
        return R.success(true);
    }
}
