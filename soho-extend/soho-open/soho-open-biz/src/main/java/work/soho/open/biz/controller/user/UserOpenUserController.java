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
import work.soho.open.biz.domain.OpenUser;
import work.soho.open.biz.service.OpenUserService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 开放平台用户Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/openUser" )
public class UserOpenUserController {

    private final OpenUserService openUserService;

    /**
     * 查询开放平台用户列表
     */
    @GetMapping("/list")
    @Node(value = "user::openUser::list", name = "获取 开放平台用户 列表")
    public R<PageSerializable<OpenUser>> list(OpenUser openUser, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenUser> lqw = new LambdaQueryWrapper<OpenUser>();
        lqw.eq(openUser.getId() != null, OpenUser::getId ,openUser.getId());
        lqw.eq(openUser.getUserId() != null, OpenUser::getUserId ,openUser.getUserId());
        lqw.like(StringUtils.isNotBlank(openUser.getUsername()),OpenUser::getUsername ,openUser.getUsername());
        lqw.like(StringUtils.isNotBlank(openUser.getPassword()),OpenUser::getPassword ,openUser.getPassword());
        lqw.like(StringUtils.isNotBlank(openUser.getName()),OpenUser::getName ,openUser.getName());
        lqw.eq(openUser.getType() != null, OpenUser::getType ,openUser.getType());
        lqw.like(StringUtils.isNotBlank(openUser.getContactName()),OpenUser::getContactName ,openUser.getContactName());
        lqw.like(StringUtils.isNotBlank(openUser.getContactEmail()),OpenUser::getContactEmail ,openUser.getContactEmail());
        lqw.like(StringUtils.isNotBlank(openUser.getContactPhone()),OpenUser::getContactPhone ,openUser.getContactPhone());
        lqw.eq(openUser.getUpdatedTime() != null, OpenUser::getUpdatedTime ,openUser.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenUser::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenUser::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<OpenUser> list = openUserService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取开放平台用户详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "user::openUser::getInfo", name = "获取 开放平台用户 详细信息")
    public R<OpenUser> getInfo(@PathVariable("id" ) Long id) {
        return R.success(openUserService.getById(id));
    }

    /**
     * 新增开放平台用户
     */
    @PostMapping
    @Node(value = "user::openUser::add", name = "新增 开放平台用户")
    public R<Boolean> add(@RequestBody OpenUser openUser) {
        return R.success(openUserService.save(openUser));
    }

    /**
     * 修改开放平台用户
     */
    @PutMapping
    @Node(value = "user::openUser::edit", name = "修改 开放平台用户")
    public R<Boolean> edit(@RequestBody OpenUser openUser) {
        return R.success(openUserService.updateById(openUser));
    }

    /**
     * 删除开放平台用户
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "user::openUser::remove", name = "删除 开放平台用户")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(openUserService.removeByIds(Arrays.asList(ids)));
    }
}