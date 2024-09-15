package work.soho.admin.cloud.biz.biz.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.biz.service.impl.AdminConfigServiceImpl;
import work.soho.admin.api.request.AdminConfigInitRequest;

@RestController
@RequestMapping("/cloud/admin/adminConfig")
@RequiredArgsConstructor
public class CloudAdminConfigController {
    private final AdminConfigServiceImpl adminConfigApiService;

    /**
     * 获取指定key的值
     *
     * @param key
     * @return
     */
    @GetMapping("getByKey")
    public String getByKey(@RequestParam String key) {
        return adminConfigApiService.getByKey(key, String.class);
    }

    /**
     * 初始化配置变量
     *
     * @param adminConfigInitRequest
     * @return
     */
    @PostMapping("initItems")
    public Boolean initItems(AdminConfigInitRequest adminConfigInitRequest) {
        return adminConfigApiService.initItems(adminConfigInitRequest);
    }
}