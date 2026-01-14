package work.soho.open.biz.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import work.soho.common.core.result.R;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.open.biz.domain.OpenUser;
import work.soho.open.biz.service.OpenUserService;

;

/**
 * 开放平台用户Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/open/user/openUser" )
public class UserOpenUserController {

    private final OpenUserService openUserService;


    /**
     * 获取开放平台用户详细信息
     */
    @GetMapping
    @Node(value = "user::openUser::getInfo", name = "获取 开放平台用户 详细信息")
    public R<OpenUser> getInfo(@AuthenticationPrincipal SohoUserDetails userDetails) {
        OpenUser openUser = openUserService.getOpenUserByUserId(userDetails.getId());
        return R.success(openUser);
    }

    /**
     * 新增开放平台用户
     */
    @PostMapping
    @Node(value = "user::openUser::add", name = "新增 开放平台用户")
    public R<Boolean> add(@RequestBody OpenUser openUser, @AuthenticationPrincipal SohoUserDetails userDetails) {
        openUser.setUserId(userDetails.getId());
        return R.success(openUserService.save(openUser));
    }

    /**
     * 修改开放平台用户
     */
    @PutMapping
    @Node(value = "user::openUser::edit", name = "修改 开放平台用户")
    public R<Boolean> edit(@RequestBody OpenUser openUser, @AuthenticationPrincipal SohoUserDetails userDetails) {
        OpenUser oldOpenUser = openUserService.getOpenUserByUserId(userDetails.getId());
        Assert.notNull(oldOpenUser, "数据不存在");
        openUser.setId(oldOpenUser.getId());
        openUser.setUserId(oldOpenUser.getUserId());
        return R.success(openUserService.updateById(openUser));
    }

}