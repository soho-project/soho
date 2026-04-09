package work.soho.admin.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.biz.domain.AdminPost;
import work.soho.admin.biz.service.AdminPostService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 岗位管理控制器。
 */
@Api(tags = "岗位管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/admin/adminPost")
public class AdminPostController {
    private final AdminPostService adminPostService;

    /**
     * 获取岗位列表。
     *
     * @param adminPost 查询条件
     * @return 岗位列表
     */
    @ApiOperation("岗位列表")
    @Node("adminPost:list")
    @GetMapping("/list")
    public R<PageSerializable<AdminPost>> list(AdminPost adminPost) {
        LambdaQueryWrapper<AdminPost> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotEmpty(adminPost.getName()), AdminPost::getName, adminPost.getName());
        lqw.like(StringUtils.isNotEmpty(adminPost.getCode()), AdminPost::getCode, adminPost.getCode());
        lqw.eq(adminPost.getStatus() != null, AdminPost::getStatus, adminPost.getStatus());
        lqw.orderByAsc(AdminPost::getSort).orderByAsc(AdminPost::getId);
        PageUtils.startPage();
        return R.success(new PageSerializable<>(adminPostService.list(lqw)));
    }

    /**
     * 获取岗位详情。
     *
     * @param id 岗位ID
     * @return 岗位详情
     */
    @ApiOperation("岗位详情")
    @Node("adminPost:details")
    @GetMapping
    public R<AdminPost> details(Long id) {
        return R.success(adminPostService.getById(id));
    }

    /**
     * 创建岗位。
     *
     * @param adminPost 岗位信息
     * @return 创建结果
     */
    @ApiOperation("创建岗位")
    @Node("adminPost:create")
    @PostMapping
    public R<String> create(@RequestBody AdminPost adminPost) {
        try {
            adminPost.setId(null);
            adminPostService.savePost(adminPost);
            return R.success("保存成功");
        } catch (IllegalArgumentException e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 更新岗位。
     *
     * @param adminPost 岗位信息
     * @return 更新结果
     */
    @ApiOperation("更新岗位")
    @Node("adminPost:update")
    @PutMapping
    public R<String> update(@RequestBody AdminPost adminPost) {
        try {
            adminPostService.savePost(adminPost);
            return R.success("保存成功");
        } catch (IllegalArgumentException e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 删除岗位。
     *
     * @param id 岗位ID
     * @return 删除结果
     */
    @ApiOperation("删除岗位")
    @Node("adminPost:delete")
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable("id") Long id) {
        try {
            adminPostService.removePost(id);
            return R.success("删除成功");
        } catch (IllegalArgumentException e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取岗位选项。
     *
     * @return 选项列表
     */
    @ApiOperation("岗位选项")
    @Node("adminPost:options")
    @GetMapping("/options")
    public R<List<OptionVo<Long, String>>> options() {
        List<OptionVo<Long, String>> options = adminPostService.list(new LambdaQueryWrapper<AdminPost>()
                        .eq(AdminPost::getStatus, 1)
                        .orderByAsc(AdminPost::getSort)
                        .orderByAsc(AdminPost::getId))
                .stream()
                .map(item -> {
                    OptionVo<Long, String> optionVo = new OptionVo<>();
                    optionVo.setValue(item.getId());
                    optionVo.setLabel(item.getName());
                    return optionVo;
                })
                .collect(Collectors.toList());
        return R.success(options);
    }
}
