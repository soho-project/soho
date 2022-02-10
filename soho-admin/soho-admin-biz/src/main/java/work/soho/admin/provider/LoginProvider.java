package work.soho.admin.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.mapper.AdminUserMapper;
import work.soho.admin.service.AdminUserService;
import work.soho.api.admin.result.Login;
import work.soho.common.core.result.R;

@Controller
@RequiredArgsConstructor
@RestController
public class LoginProvider {
    private final AdminUserService adminUserService;

    @PostMapping("/login")
    public R<Login> login(@RequestParam(name = "username", required = true) String username, @RequestParam(name = "password", required = true) String password) {
        Login loginResult = new Login();
        loginResult.setToken(adminUserService.login(username, password));
        return R.ok(loginResult);
    }
}
