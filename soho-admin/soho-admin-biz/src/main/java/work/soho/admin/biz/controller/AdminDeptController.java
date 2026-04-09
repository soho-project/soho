package work.soho.admin.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import work.soho.admin.biz.domain.AdminDept;
import work.soho.admin.biz.service.AdminDeptService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门管理控制器。
 */
@Api(tags = "部门管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/admin/adminDept")
public class AdminDeptController {
    private final AdminDeptService adminDeptService;

    /**
     * 获取部门树。
     *
     * @return 部门树
     */
    @ApiOperation("部门树")
    @Node("adminDept:tree")
    @GetMapping("/tree")
    public R<List<AdminDept>> tree() {
        return R.success(adminDeptService.tree());
    }

    /**
     * 获取部门列表。
     *
     * @param adminDept 查询条件
     * @return 部门列表
     */
    @ApiOperation("部门列表")
    @Node("adminDept:list")
    @GetMapping("/list")
    public R<List<AdminDept>> list(AdminDept adminDept) {
        LambdaQueryWrapper<AdminDept> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotEmpty(adminDept.getName()), AdminDept::getName, adminDept.getName());
        lqw.eq(adminDept.getStatus() != null, AdminDept::getStatus, adminDept.getStatus());
        lqw.orderByAsc(AdminDept::getSort).orderByAsc(AdminDept::getId);
        return R.success(adminDeptService.list(lqw));
    }

    /**
     * 获取部门详情。
     *
     * @param id 部门ID
     * @return 部门详情
     */
    @ApiOperation("部门详情")
    @Node("adminDept:details")
    @GetMapping
    public R<AdminDept> details(Long id) {
        return R.success(adminDeptService.getById(id));
    }

    /**
     * 创建部门。
     *
     * @param adminDept 部门信息
     * @return 创建结果
     */
    @ApiOperation("创建部门")
    @Node("adminDept:create")
    @PostMapping
    public R<String> create(@RequestBody AdminDept adminDept) {
        try {
            adminDept.setId(null);
            adminDeptService.saveDept(adminDept);
            return R.success("保存成功");
        } catch (IllegalArgumentException e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 更新部门。
     *
     * @param adminDept 部门信息
     * @return 更新结果
     */
    @ApiOperation("更新部门")
    @Node("adminDept:update")
    @PutMapping
    public R<String> update(@RequestBody AdminDept adminDept) {
        try {
            adminDeptService.saveDept(adminDept);
            return R.success("保存成功");
        } catch (IllegalArgumentException e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 删除部门。
     *
     * @param id 部门ID
     * @return 删除结果
     */
    @ApiOperation("删除部门")
    @Node("adminDept:delete")
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable("id") Long id) {
        try {
            adminDeptService.removeDept(id);
            return R.success("删除成功");
        } catch (IllegalArgumentException e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取部门选项。
     *
     * @return 选项列表
     */
    @ApiOperation("部门选项")
    @Node("adminDept:options")
    @GetMapping("/options")
    public R<List<OptionVo<Long, String>>> options() {
        List<OptionVo<Long, String>> options = adminDeptService.list(new LambdaQueryWrapper<AdminDept>()
                        .eq(AdminDept::getStatus, 1)
                        .orderByAsc(AdminDept::getSort)
                        .orderByAsc(AdminDept::getId))
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
