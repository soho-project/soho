package work.soho.admin.biz.controller.client;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.biz.domain.AdminRelease;
import work.soho.admin.biz.service.AdminReleaseService;
import work.soho.admin.api.service.AdminDictApiService;
import work.soho.common.core.result.R;

import java.util.Optional;

@Api(tags = "客户端版本信息API")
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
        Optional<Integer> platformType = adminDictApiService.getOptionsByCode("adminRelease-platformType").stream().filter(item->item.getLabel().equals(name)).map(item->item.getValue()).findFirst();
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
