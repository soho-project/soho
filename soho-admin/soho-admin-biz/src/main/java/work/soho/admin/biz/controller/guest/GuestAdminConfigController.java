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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/guest/adminConfig" )
@Api(tags = "访客系统配置信息")
public class GuestAdminConfigController {
    private final AdminConfigService adminConfigService;

    @GetMapping("/roleConfig")
    public R<Map<String, Object>> roleConfig(String roleName) {
        Map<String, Object> result = new HashMap<>();
        // 检查对应前端配置信息前缀
        String prefix = adminConfigService.getByKey("common-admin-front-config-prefix");
        if(prefix != null && !prefix.isEmpty()) {
            // 查询指定前置的所有配置信息
            LambdaQueryWrapper<AdminConfig> lqw = new LambdaQueryWrapper<>();
            lqw.likeRight(AdminConfig::getKey, prefix);
            List<AdminConfig> list = adminConfigService.list(lqw);
            list.forEach(item-> result.put(item.getKey(), item.getValue()));
        }

        return R.success(result);
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
        if(keys == null || keys == "") {
            return R.error();
        }
        List< String> configKeys = Arrays.stream(keys.split(",")).collect(Collectors.toList());
        // 检查key是否在 configKeys 中
        if(!configKeys.contains(key)) {
            return R.error("非法访问");
        }
        String accessKeys = adminConfigService.getByKey(key);
        List< String> accessKeyList = Arrays.stream(accessKeys.split(",")).collect(Collectors.toList());

        HashMap<String, Object> result = new HashMap<>();
        accessKeyList.forEach(item->{
            result.put(item, adminConfigService.getByKey(item));
        });
        return R.success(result);
    }
}
