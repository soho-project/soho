package work.soho.open.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.api.admin.annotation.Node;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.open.biz.domain.OpenUser;
import work.soho.open.biz.service.OpenUserService;

import java.util.Arrays;
import java.util.List;
/**
 * 开放平台用户Controller
 *
 * @author fang
 */
@Api(tags = "开放平台用户")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/openUser" )
public class OpenUserController {

    private final OpenUserService openUserService;

    /**
     * 查询开放平台用户列表
     */
    @GetMapping("/list")
    @Node(value = "openUser::list", name = "开放平台用户列表")
    public R<PageSerializable<OpenUser>> list(OpenUser openUser)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenUser> lqw = new LambdaQueryWrapper<OpenUser>();
        lqw.eq(openUser.getId() != null, OpenUser::getId ,openUser.getId());
        lqw.eq(openUser.getUid() != null, OpenUser::getUid ,openUser.getUid());
        lqw.like(StringUtils.isNotBlank(openUser.getOriginId()),OpenUser::getOriginId ,openUser.getOriginId());
        lqw.eq(openUser.getAppId() != null, OpenUser::getAppId ,openUser.getAppId());
        lqw.eq(openUser.getCreateTime() != null, OpenUser::getCreateTime ,openUser.getCreateTime());
        lqw.eq(openUser.getUpdatedTime() != null, OpenUser::getUpdatedTime ,openUser.getUpdatedTime());
        List<OpenUser> list = openUserService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取开放平台用户详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "openUser::getInfo", name = "开放平台用户详细信息")
    public R<OpenUser> getInfo(@PathVariable("id" ) Long id) {
        return R.success(openUserService.getById(id));
    }

    /**
     * 新增开放平台用户
     */
    @PostMapping
    @Node(value = "openUser::add", name = "开放平台用户新增")
    public R<Boolean> add(@RequestBody OpenUser openUser) {
        return R.success(openUserService.save(openUser));
    }

    /**
     * 修改开放平台用户
     */
    @PutMapping
    @Node(value = "openUser::edit", name = "开放平台用户修改")
    public R<Boolean> edit(@RequestBody OpenUser openUser) {
        return R.success(openUserService.updateById(openUser));
    }

    /**
     * 删除开放平台用户
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "openUser::remove", name = "开放平台用户删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(openUserService.removeByIds(Arrays.asList(ids)));
    }
}