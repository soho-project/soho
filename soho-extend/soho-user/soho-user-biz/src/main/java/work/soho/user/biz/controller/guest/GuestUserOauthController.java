package work.soho.user.biz.controller.guest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.common.core.result.R;
import work.soho.user.api.request.UserOauthLoginRequest;
import work.soho.user.biz.config.UserSysConfig;
import work.soho.user.biz.service.UserOauthService;

import java.util.Map;

;

/**
 * 用户三方认证Controller
 *
 * @author fang
 */
@Api(value = "guest 用户三方认证", tags = "guest 用户三方认证")
@RequiredArgsConstructor
@RestController
@RequestMapping("user/guest/userOauth" )
public class GuestUserOauthController {

    private final UserOauthService userOauthService;

    private final UserSysConfig userSysConfig;

    /**
     * 三方认证登录
     */
    @ApiOperation("三方认证登录")
    @PostMapping(value = "login")
    public R<Map<String, String>> login(@RequestBody UserOauthLoginRequest userOauthLoginRequest) {
        if(!userSysConfig.getOpenThirdLogin()) {
            return R.error("未开启三方登录");
        }
        return R.success(userOauthService.loginWithCode(userOauthLoginRequest.getCode()));
    }

    /**
     * 三方认证登录
     */
    @ApiOperation("三方认证登录")
    @GetMapping(value = "login")
    public R<Map<String, String>> getLogin(String code) {
        if(!userSysConfig.getOpenThirdLogin()) {
            return R.error("未开启三方登录");
        }
        return R.success(userOauthService.loginWithCode(code));
    }

}