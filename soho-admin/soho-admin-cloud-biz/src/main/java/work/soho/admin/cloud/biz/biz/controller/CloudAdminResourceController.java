package work.soho.admin.cloud.biz.biz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.api.service.AdminResourceApiService;

import java.util.List;

@RestController
@RequestMapping("/cloud/admin/adminResource")
@RequiredArgsConstructor
public class CloudAdminResourceController {
    private final AdminResourceApiService adminResourceApiService;

    /**
     * 获取角色对应的资源key列表
     *
     * @param roleName
     * @return
     */
    @GetMapping("getResourceKeyListByRole")
    public List<String> getResourceKeyListByRole(String roleName)
    {
        return adminResourceApiService.getResourceKeyListByRole(roleName);
    }
}
