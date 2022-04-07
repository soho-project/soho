package work.soho.admin.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import java.util.Arrays;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.util.StringUtils;
import com.github.pagehelper.Page;
import work.soho.common.core.result.R;
import work.soho.admin.domain.AdminConfig;
import work.soho.admin.service.AdminConfigService;

/**
 * admin_configController
 *
 * @author i
 * @date 2022-04-05 23:20:12
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/adminConfig" )
public class AdminConfigController extends BaseController {

    private final AdminConfigService adminConfigService;

    /**
     * 查询admin_config列表
     */
    @GetMapping("/list")
    public R<PageSerializable<AdminConfig>> list(AdminConfig adminConfig)
    {
        startPage();
        LambdaQueryWrapper<AdminConfig> lqw = new LambdaQueryWrapper<AdminConfig>();

        if (adminConfig.getId() != null){
            lqw.eq(AdminConfig::getId ,adminConfig.getId());
        }
        if (StringUtils.isNotBlank(adminConfig.getGroupKey())){
            lqw.eq(AdminConfig::getGroupKey ,adminConfig.getGroupKey());
        }
        if (StringUtils.isNotBlank(adminConfig.getKey())){
            lqw.like(AdminConfig::getKey ,adminConfig.getKey());
        }
        if (StringUtils.isNotBlank(adminConfig.getValue())){
            lqw.like(AdminConfig::getValue ,adminConfig.getValue());
        }
        if (adminConfig.getType() != null){
            lqw.eq(AdminConfig::getType ,adminConfig.getType());
        }
        if (adminConfig.getUpdatedTime() != null){
            lqw.eq(AdminConfig::getUpdatedTime ,adminConfig.getUpdatedTime());
        }
        if (adminConfig.getCreatedTime() != null){
            lqw.eq(AdminConfig::getCreatedTime ,adminConfig.getCreatedTime());
        }
        List<AdminConfig> list = adminConfigService.list(lqw);
        return R.success(new PageSerializable(list));
    }

    /**
     * 获取admin_config详细信息
     */
    @GetMapping(value = "/{id}" )
    public R<AdminConfig> getInfo(@PathVariable("id" ) Long id) {
        return R.success(adminConfigService.getById(id));
    }

    /**
     * 新增admin_config
     */
    @PostMapping
    public R<Boolean> add(@RequestBody AdminConfig adminConfig) {
        return R.success(adminConfigService.save(adminConfig));
    }

    /**
     * 修改admin_config
     */
    @PutMapping
    public R<Boolean> edit(@RequestBody AdminConfig adminConfig) {
        return R.success(adminConfigService.updateById(adminConfig));
    }

    /**
     * 删除admin_config
     */
    @DeleteMapping("/{ids}" )
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(adminConfigService.removeByIds(Arrays.asList(ids)));
    }
}
