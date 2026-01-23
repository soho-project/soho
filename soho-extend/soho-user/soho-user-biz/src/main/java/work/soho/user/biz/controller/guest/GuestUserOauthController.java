package work.soho.user.biz.controller.guest;

import cn.hutool.core.lang.Assert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.enums.AuthUserGender;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.*;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.web.bind.annotation.*;
import work.soho.common.core.result.R;
import work.soho.user.api.dto.ThridOauthDto;
import work.soho.user.api.request.UserOauthLoginRequest;
import work.soho.user.biz.config.UserSysConfig;
import work.soho.user.biz.domain.UserOauthType;
import work.soho.user.biz.enums.UserOauthTypeEnums;
import work.soho.user.biz.service.UserOauthService;
import work.soho.user.biz.service.UserOauthTypeService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    private final UserOauthTypeService userOauthTypeService;

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

    @RequestMapping("/render/{type}")
    public void renderAuth(HttpServletResponse response, @PathVariable("type") Integer type) throws IOException {
        AuthRequest authRequest = getAuthRequest(type);
        response.sendRedirect(authRequest.authorize(AuthStateUtils.createState()));
    }

    @RequestMapping("/callback/{key}")
    public Object login(AuthCallback callback, @PathVariable("type") Integer type) {
        AuthRequest authRequest = getAuthRequest(type);
        AuthResponse<AuthUser> authResponse = authRequest.login(callback);
        if (authResponse.ok()) {
            ThridOauthDto thridOauthDto = new ThridOauthDto();
            thridOauthDto.setOpenId(authResponse.getData().getUuid());
            thridOauthDto.setUsername(authResponse.getData().getUsername());
            thridOauthDto.setNickname(authResponse.getData().getNickname());
            thridOauthDto.setAvatar(authResponse.getData().getAvatar());

            // 设置性别
            if(authResponse.getData().getGender() != null) {
                if(AuthUserGender.MALE.equals(authResponse.getData().getGender())) {
                    thridOauthDto.setGender(1);
                }

                if(AuthUserGender.FEMALE.equals(authResponse.getData().getGender())) {
                    thridOauthDto.setGender(2);
                }
            }

            // 鉴权相关的信息
            thridOauthDto.setAccessToken(authResponse.getData().getToken().getAccessToken());
            thridOauthDto.setExpireIn(authResponse.getData().getToken().getExpireIn());
            thridOauthDto.setRefreshToken(authResponse.getData().getToken().getRefreshToken());
            thridOauthDto.setRefreshTokenExpireIn(authResponse.getData().getToken().getRefreshTokenExpireIn());

            thridOauthDto.setPlatformId(type);

            return userOauthService.loginWithThridOauth(thridOauthDto);
        }
        return authRequest.login(callback);
    }

    private AuthRequest getAuthRequest(Integer type) {
        UserOauthType userOauthType = userOauthTypeService.getById( type);
        Assert.notNull(userOauthType, "未找到三方认证类型");

        UserOauthTypeEnums.Adapter adapter = UserOauthTypeEnums.Adapter.getById(userOauthType.getAdapter());
        Assert.notNull(adapter, "未找到三方认证适配器");

        AuthConfig authConfig = buildAuthConfig(userOauthType, type);
        switch (adapter) {
            case GITHUB:
                return new AuthGithubRequest(authConfig);
            case GITEE:
                return new AuthGiteeRequest(authConfig);
            case WECHAT_MINI_PROGRAM:
                return new AuthWechatMiniProgramRequest(authConfig);
            case WECHAT_MP:
                return new AuthWeChatMpRequest(authConfig);
            case WECHAT_OPEN_PLATFORM:
                return new AuthWeChatOpenRequest(authConfig);
            case QQ:
                return new AuthQqRequest(authConfig);
            case QQ_MINI_PROGRAM:
                return new AuthQQMiniProgramRequest(authConfig);
            case SINA_WEIBO:
                return new AuthWeiboRequest(authConfig);
            case BAIDU:
                return new AuthBaiduRequest(authConfig);
            case ALIPAY_PUBLIC_KEY:
                return new AuthAlipayRequest(authConfig);
//            case ALIPAY_CERTIFICATE:
//                return new AuthAlipayCertRequest(authConfig);
            case TWITTER:
                return new AuthTwitterRequest(authConfig);
            case LINKEDIN:
                return new AuthLinkedinRequest(authConfig);
            case LARK:
                return new AuthFeishuRequest(authConfig);
            case GOOGLE:
                return new AuthGoogleRequest(authConfig);
            case FACEBOOK:
                return new AuthFacebookRequest(authConfig);
            case MICROSOFT:
                return new AuthMicrosoftRequest(authConfig);
            case MICROSOFT_CN:
                return new AuthMicrosoftCnRequest(authConfig);
            case ALIBABA_CLOUD:
                return new AuthAliyunRequest(authConfig);
            case HUAWEI:
                return new AuthHuaweiRequest(authConfig);
            case HUAWEI_V3:
                return new AuthHuaweiV3Request(authConfig);
            case TIKTOK:
                return new AuthDouyinRequest(authConfig);
            case JD:
                return new AuthJdRequest(authConfig);
            case TAOBAO:
                return new AuthTaobaoRequest(authConfig);
            case OPEN_SOURCE_CHINA:
                return new AuthOschinaRequest(authConfig);
            case PROGRAMMERS_INN:
                return new AuthProginnRequest(authConfig);
            case CSDN:
                return new AuthCsdnRequest(authConfig);
            case DINGTALK:
                return new AuthDingTalkRequest(authConfig);
            case DINGTALK_ACCOUNT:
                return new AuthDingTalkAccountRequest(authConfig);
            case MI:
                return new AuthMiRequest(authConfig);
            case TOUTIAO:
                return new AuthToutiaoRequest(authConfig);
            case TEAMBITION:
                return new AuthTeambitionRequest(authConfig);
            case RENREN:
                return new AuthRenrenRequest(authConfig);
            case PINTEREST:
                return new AuthPinterestRequest(authConfig);
            case GITLAB:
                return new AuthGitlabRequest(authConfig);
            case MEITUAN:
                return new AuthMeituanRequest(authConfig);
            case ELEME:
                return new AuthElemeRequest(authConfig);
            case AMAZON:
                return new AuthAmazonRequest(authConfig);
            case SLACK:
                return new AuthSlackRequest(authConfig);
            case LINE:
                return new AuthLineRequest(authConfig);
            case AFDIAN:
                return new AuthAfDianRequest(authConfig);
            case FIGMA:
                return new AuthFigmaRequest(authConfig);
            case KUJIALE:
                return new AuthKujialeRequest(authConfig);

        }
        throw new RuntimeException("未定义的三方平台");
    }

    private AuthConfig buildAuthConfig(UserOauthType userOauthType, Integer type) {
        AuthConfig authConfig = AuthConfig.builder()
                .clientId(userOauthType.getClientId())
                .clientSecret(userOauthType.getClientSecret())
                .redirectUri("/user/guest/userOauth/callback/" + type)
                .build();
        return authConfig;
    }
}
