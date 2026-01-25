package work.soho.user.biz.controller.open.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.user.api.vo.OpenUserInfoVo;
import work.soho.user.biz.service.UserInfoService;
import work.soho.open.api.annotation.OpenApiDoc;

@Log4j2
@Api(tags = "开放平台用户用户信息", value = "开放平台用户用户信息")
@OpenApiDoc(value = "用户信息", name = "用户信息", description = "用户信息")
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/open/user/userInfo" )
public class OpenUserUserInfoController {
    private final UserInfoService userInfoService;
    /**
     * 获取用户信息
     *
     * @param sohoUserDetails
     * @return
     */
    @GetMapping
    @ApiOperation("获取用户信息")
    @OpenApiDoc(value = "获取用户信息", name = "获取用户信息", description = "获取用户信息", authRole = "openUser")
    public R<OpenUserInfoVo> getUserInfo(@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        OpenUserInfoVo openUserInfoVo = BeanUtils.copy(userInfoService.getById(sohoUserDetails.getId()), OpenUserInfoVo.class);
        return R.success(openUserInfoVo);
    }
}
