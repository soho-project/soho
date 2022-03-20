package work.soho.admin.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.domain.AdminUser;
import work.soho.admin.service.AdminUserService;
import work.soho.admin.service.impl.TokenServiceImpl;
import work.soho.admin.service.impl.UserDetailsServiceImpl;
import work.soho.common.core.result.R;

@RequiredArgsConstructor
@RestController
public class AdminUserProvider {
    private final TokenServiceImpl tokenService;
    private final AdminUserService adminUserService;

    @GetMapping("/user")
    public R user() {
        UserDetailsServiceImpl.UserDetailsImpl userDetails = tokenService.getLoginUser();
        AdminUser adminUser = adminUserService.getById(userDetails.getId());
        return R.ok(adminUser);
    }
}
