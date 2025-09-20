package work.soho.admin.biz.controller.guest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.biz.domain.AdminConfig;
import work.soho.admin.biz.service.AdminConfigService;
import work.soho.common.core.result.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
