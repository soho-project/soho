package work.soho.admin.biz.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.biz.domain.AdminConfigGroup;
import work.soho.admin.biz.service.AdminConfigGroupService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * admin_config_groupController
 *
 * @author i
 * @date 2022-04-05 23:20:13
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/admin/adminConfigGroup" )
@Api(tags = "配置分组")
public class AdminConfigGroupController extends BaseController {

    private final AdminConfigGroupService adminConfigGroupService;

    /**
     * 查询admin_config_group列表
     */
    @Node(value = "config::listGroup", name = "分组列表")
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
        lqw.orderByDesc(AdminConfigGroup::getId);
        List<AdminConfigGroup> list = adminConfigGroupService.list(lqw);
        return R.success((Page<AdminConfigGroup>)list);
    }

    /**
     * 获取admin_config_group详细信息
     */
    @Node(value = "config::getInfoGroup", name = "分组详情")
    @GetMapping(value = "/{id}" )
    public R<AdminConfigGroup> getInfo(@PathVariable("id" ) Long id) {
        return R.success(adminConfigGroupService.getById(id));
    }

    /**
     * 新增admin_config_group
     */
    @Node(value = "config::addGroup", name = "新增分组")
    @PostMapping
    public R<Boolean> add(@RequestBody AdminConfigGroup adminConfigGroup) {
        return R.success(adminConfigGroupService.save(adminConfigGroup));
    }

    /**
     * 修改admin_config_group
     */
    @Node(value = "config::editGroup", name = "编辑分组")
    @PutMapping
    public R<Boolean> edit(@RequestBody AdminConfigGroup adminConfigGroup) {
        return R.success(adminConfigGroupService.updateById(adminConfigGroup));
    }

    /**
     * 删除admin_config_group
     */
    @Node(value = "config::removeGroup", name = "删除分组")
    @DeleteMapping("/{ids}" )
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(adminConfigGroupService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取配置分组选项
     *
     * @return
     */
    @GetMapping("options")
    public R<List<OptionVo<String, String>>> options() {
        List<AdminConfigGroup> adminConfigGroupList = adminConfigGroupService.list();
        List<OptionVo<String, String>> list = new ArrayList<>();

        for(AdminConfigGroup item: adminConfigGroupList) {
            OptionVo<String, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getKey());
            optionVo.setLabel(item.getName());
            list.add(optionVo);
        }
        return R.success(list);
    }
}
