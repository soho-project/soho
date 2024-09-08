package work.soho.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.api.admin.service.AdminInfoApiService;
import work.soho.api.admin.vo.AdminUserVo;

import java.util.Set;

@RestController
@RequestMapping("/cloud/admin/adminInfo")
@RequiredArgsConstructor
public class CloudAdminInfoController {
    private final AdminInfoApiService service;

    @GetMapping(value = "getAdminById")
    public AdminUserVo getAdminById(Long id) {
        return service.getAdminById(id);
    }

    @GetMapping("getResourceKeys")
    public Set<String> getResourceKeys(Long id) {
        return service.getResourceKeys(id);
    }
}
