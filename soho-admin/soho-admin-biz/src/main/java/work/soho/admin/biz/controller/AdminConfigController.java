package work.soho.admin.biz.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.biz.domain.AdminConfig;
import work.soho.admin.biz.service.AdminConfigService;
import work.soho.common.security.annotation.Node;
import work.soho.common.core.result.R;
import work.soho.common.core.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * admin_configController
 *
 * @author i
 * @date 2022-04-05 23:20:12
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/admin/adminConfig" )
@Api(tags = "系统配置信息")
public class AdminConfigController extends BaseController {

    private final AdminConfigService adminConfigService;

    /**
     * 查询admin_config列表
     */
    @Node("config::list")
    @GetMapping("/list")
    public R<PageSerializable<AdminConfig>> list(AdminConfig adminConfig)
    {
        startPage();
        LambdaQueryWrapper<AdminConfig> lqw = new LambdaQueryWrapper<>();

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
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取admin_config详细信息
     */
    @Node("config::getInfo")
    @GetMapping(value = "/{id}" )
    public R<AdminConfig> getInfo(@PathVariable("id" ) Long id) {
        return R.success(adminConfigService.getById(id));
    }

    /**
     * 新增admin_config
     */
    @Node("config::add")
    @PostMapping
    public R<Boolean> add(@RequestBody AdminConfig adminConfig) {
        return R.success(adminConfigService.save(adminConfig));
    }

    /**
     * 修改admin_config
     */
    @Node("config::edit")
    @PutMapping
    public R<Boolean> edit(@RequestBody AdminConfig adminConfig) {
        return R.success(adminConfigService.updateById(adminConfig));
    }

    /**
     * 删除admin_config
     */
    @Node("config::remove")
    @DeleteMapping("/{ids}" )
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(adminConfigService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 后台通用配置
     *
     * 配置信息缓存到客户端
     *
     * @return
     */
    @GetMapping("/common")
    public R<Map<String,Object>> common() {
        Map<String,Object> result = new HashMap<>();
        String keys = adminConfigService.getByKey("admin-common-config");
        if(keys == null || keys == "") {
            return R.success(result);
        }

        List<String> configKeys = Arrays.stream(keys.split(";")).collect(Collectors.toList());
        configKeys.stream().forEach(item->{
            result.put(item, adminConfigService.getByKey(item));
        });
        return R.success(result);
    }
}
