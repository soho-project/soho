package work.soho.user.biz.controller.user;

import java.time.LocalDateTime;
import work.soho.common.core.util.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.*;
import lombok.RequiredArgsConstructor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.util.StringUtils;
import com.github.pagehelper.PageSerializable;
import work.soho.common.core.result.R;
import work.soho.common.security.annotation.Node;;
import work.soho.user.biz.domain.UserOauth;
import work.soho.user.biz.service.UserOauthService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import work.soho.common.security.userdetails.SohoUserDetails;
import org.springframework.util.Assert;

/**
 * 用户三方认证Controller
 *
 * @author fang
 */
@Api(value = "user 用户三方认证", tags = "user 用户三方认证")
@RequiredArgsConstructor
@RestController
@RequestMapping("user/user/userOauth" )
public class UserUserOauthController {

    private final UserOauthService userOauthService;


    /**
     * 获取用户三方认证详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "user::userOauth::getInfo", name = "获取 用户三方认证 详细信息")
    @ApiOperation(value = "user 获取 用户三方认证 详细信息", notes = "user 获取 用户三方认证 详细信息")
    public R<UserOauth> getInfo(@PathVariable("id" ) Long id, @AuthenticationPrincipal SohoUserDetails userDetails) {
        UserOauth userOauth = userOauthService.getById(id);
        return R.success(userOauth);
    }

}