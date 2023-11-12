package work.soho.admin.controller.client;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.ehcache.core.spi.service.ServiceFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.domain.AdminRelease;
import work.soho.admin.service.AdminReleaseService;
import work.soho.api.admin.service.AdminDictApiService;
import work.soho.common.core.result.R;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/client/api/admin/adminRelease")
public class ClientAdminReleaseController {
    private final AdminDictApiService adminDictApiService;
    private final AdminReleaseService adminReleaseService;

    /**
     * 根据平台名获取信息
     *
     * @param name
     * @return
     */
    @GetMapping("/getByPlatform")
    public R<AdminRelease> getByPlatform(String name) {
        Optional<Integer> platformType = adminDictApiService.getOptionsByCode("release_platform").stream().filter(item->item.getLabel().equals(name)).map(item->item.getValue()).findFirst();
        if(!platformType.isPresent()) {
            return R.error("不存在");
        }
        LambdaQueryWrapper<AdminRelease> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AdminRelease::getPlatformType, platformType.get());
        lambdaQueryWrapper.orderByDesc(AdminRelease::getId);
        lambdaQueryWrapper.last("limit 1");
        AdminRelease adminRelease = adminReleaseService.getOne(lambdaQueryWrapper);
        return R.success(adminRelease);
    }
}
