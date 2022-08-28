package work.soho.admin.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import java.util.Arrays;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.api.admin.annotation.Node;
import work.soho.common.core.util.StringUtils;
import com.github.pagehelper.Page;
import work.soho.common.core.result.R;
import work.soho.admin.domain.AdminConfigGroup;
import work.soho.admin.service.AdminConfigGroupService;

/**
 * admin_config_groupController
 *
 * @author i
 * @date 2022-04-05 23:20:13
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/adminConfigGroup" )
@Api(tags = "配置分组")
public class AdminConfigGroupController extends BaseController {

    private final AdminConfigGroupService adminConfigGroupService;

    /**
     * 查询admin_config_group列表
     */
    @Node("adminConfigGroup:list")
    @GetMapping("/list")
    public R<Page<AdminConfigGroup>> list(AdminConfigGroup adminConfigGroup)
    {
        startPage();
        LambdaQueryWrapper<AdminConfigGroup> lqw = new LambdaQueryWrapper<>();

        if (adminConfigGroup.getId() != null){
            lqw.eq(AdminConfigGroup::getId ,adminConfigGroup.getId());
        }
        if (StringUtils.isNotBlank(adminConfigGroup.getKey())){
            lqw.like(AdminConfigGroup::getKey ,adminConfigGroup.getKey());
        }
        if (StringUtils.isNotBlank(adminConfigGroup.getName())){
            lqw.like(AdminConfigGroup::getName ,adminConfigGroup.getName());
        }
        if (adminConfigGroup.getCreatedTime() != null){
            lqw.eq(AdminConfigGroup::getCreatedTime ,adminConfigGroup.getCreatedTime());
        }
        List<AdminConfigGroup> list = adminConfigGroupService.list(lqw);
        return R.success((Page<AdminConfigGroup>)list);
    }

    /**
     * 获取admin_config_group详细信息
     */
    @Node("adminCofigGroup:getInfo")
    @GetMapping(value = "/{id}" )
    public R<AdminConfigGroup> getInfo(@PathVariable("id" ) Long id) {
        return R.success(adminConfigGroupService.getById(id));
    }

    /**
     * 新增admin_config_group
     */
    @Node("adminConfigGroup:add")
    @PostMapping
    public R<Boolean> add(@RequestBody AdminConfigGroup adminConfigGroup) {
        return R.success(adminConfigGroupService.save(adminConfigGroup));
    }

    /**
     * 修改admin_config_group
     */
    @Node("adminConfigGroup:edit")
    @PutMapping
    public R<Boolean> edit(@RequestBody AdminConfigGroup adminConfigGroup) {
        return R.success(adminConfigGroupService.updateById(adminConfigGroup));
    }

    /**
     * 删除admin_config_group
     */
    @Node("adminConfigGroup:remove")
    @DeleteMapping("/{ids}" )
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(adminConfigGroupService.removeByIds(Arrays.asList(ids)));
    }
}
