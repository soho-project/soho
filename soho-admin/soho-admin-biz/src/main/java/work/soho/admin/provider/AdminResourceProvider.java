package work.soho.admin.provider;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.annotation.Node;
import work.soho.admin.service.AdminResourceService;
import work.soho.common.core.result.R;

@Api(tags = "菜单管理")
@RestController
@RequiredArgsConstructor
public class AdminResourceProvider {
    private final AdminResourceService adminResourceService;

    @ApiOperation("同步菜单")
    @Node(value = "admin-resource-sync", name = "同比项目资源", describe = "")
    @GetMapping("/admin-resource/sync")
    public R<Boolean> syncResource() {
        adminResourceService.syncResource2Db();
        return R.ok();
    }
}
