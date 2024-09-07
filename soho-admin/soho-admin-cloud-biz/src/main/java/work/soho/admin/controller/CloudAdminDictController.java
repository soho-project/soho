package work.soho.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.api.admin.service.AdminDictApiService;

@RestController
@RequestMapping("/cloud/admin/adminDict")
@RequiredArgsConstructor
public class CloudAdminDictController {
    private final AdminDictApiService adminDictApiService;

    @RequestMapping("getMapByCode")
    public Object getMapByCode(String code) {
        return adminDictApiService.getMapByCode(code);
    }

    @RequestMapping("getOptionsByCode")
    public Object getOptionsByCode(String code) {
        return adminDictApiService.getOptionsByCode(code);
    }
}
