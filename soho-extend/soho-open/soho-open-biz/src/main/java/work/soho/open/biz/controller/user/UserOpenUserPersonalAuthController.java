package work.soho.open.biz.controller.user;

import java.time.LocalDateTime;
import work.soho.common.core.util.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.*;
import lombok.RequiredArgsConstructor;
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
import work.soho.open.biz.domain.OpenUserPersonalAuth;
import work.soho.open.biz.service.OpenUserPersonalAuthService;
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
 * 个人实名认证详情表Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("open/user/openUserPersonalAuth" )
public class UserOpenUserPersonalAuthController {

    private final OpenUserPersonalAuthService openUserPersonalAuthService;


    /**
     * 获取个人实名认证详情表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "user::openUserPersonalAuth::getInfo", name = "获取 个人实名认证详情表 详细信息")
    public R<OpenUserPersonalAuth> getInfo(@PathVariable("id" ) Long id, @AuthenticationPrincipal SohoUserDetails userDetails) {
        OpenUserPersonalAuth openUserPersonalAuth = openUserPersonalAuthService.getById(id);
        if (!openUserPersonalAuth.getUserId().equals(userDetails.getId())) {
            return R.error("数据不存在");
        }
        return R.success(openUserPersonalAuth);
    }

    /**
     * 修改个人实名认证详情表
     */
    @PutMapping
    @Node(value = "user::openUserPersonalAuth::edit", name = "修改 个人实名认证详情表")
    public R<Boolean> edit(@RequestBody OpenUserPersonalAuth openUserPersonalAuth, @AuthenticationPrincipal SohoUserDetails userDetails) {
        OpenUserPersonalAuth oldOpenUserPersonalAuth = openUserPersonalAuthService.getById(openUserPersonalAuth.getId());
        Assert.notNull(oldOpenUserPersonalAuth, "数据不存在");
        if(!oldOpenUserPersonalAuth.getUserId().equals(userDetails.getId())) {
            return R.error("无权限");
        }
        return R.success(openUserPersonalAuthService.updateById(openUserPersonalAuth));
    }

}