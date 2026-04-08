package work.soho.admin.biz.controller.guest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.biz.domain.AdminConfig;
import work.soho.admin.biz.service.AdminConfigService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.StringUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/guest/adminConfig" )
@Api(tags = "访客系统配置信息")
public class GuestAdminConfigController {
    private final AdminConfigService adminConfigService;

    /**
     * 获取后台前端公开配置。
     *
     * @param roleName 角色名称
     * @return 配置结果
     */
    @GetMapping("/roleConfig")
    public R<Map<String, Object>> roleConfig(String roleName) {
        return R.success(loadConfigsByPrefix(adminConfigService.getByKey("common-admin-front-config-prefix")));
    }

    /**
     * 获取聚合配置信息
     *
     * @return
     */
    @ApiOperation(value = "获取聚合配置信息", notes = "获取聚合配置信息; 入参Key为 多配置聚合类型的key; 前提瞧见是在公共配置中将该配置key配置为允许guest访问")
    @GetMapping("/configCollect/{key}")
    public R<Map<String, Object>> config(@PathVariable("key") String key) {
        String keys = adminConfigService.getByKey("common-guest-access-keys");
        if (StringUtils.isBlank(keys)) {
            return R.error();
        }
        List<String> configKeys = splitConfigKeys(keys);
        if (!configKeys.contains(key)) {
            return R.error("非法访问");
        }
        String accessKeys = adminConfigService.getByKey(key);
        if (StringUtils.isBlank(accessKeys)) {
            return R.success(new LinkedHashMap<>());
        }
        List<String> accessKeyList = splitConfigKeys(accessKeys);
        Map<String, Object> result = new LinkedHashMap<>();
        result.putAll(adminConfigService.getByKeys(accessKeyList));
        return R.success(result);
    }

    /**
     * 按分隔符拆分配置 key。
     *
     * @param keys 原始 key 字符串
     * @return 配置 key 列表
     */
    private List<String> splitConfigKeys(String keys) {
        return Arrays.stream(keys.split("[;,]"))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 按前缀加载配置集合。
     *
     * @param prefix 配置前缀
     * @return 配置结果
     */
    private Map<String, Object> loadConfigsByPrefix(String prefix) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (StringUtils.isBlank(prefix)) {
            return result;
        }
        LambdaQueryWrapper<AdminConfig> lqw = new LambdaQueryWrapper<>();
        lqw.likeRight(AdminConfig::getKey, prefix.trim());
        List<AdminConfig> list = adminConfigService.list(lqw);
        list.forEach(item -> result.put(item.getKey(), item.getValue()));
        return result;
    }
}
