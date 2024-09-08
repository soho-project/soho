package work.soho.admin.cloud.bridge.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.admin.cloud.bridge.feign.AdminInfoApiServiceFeign;
import work.soho.api.admin.service.AdminInfoApiService;
import work.soho.api.admin.vo.AdminUserVo;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class AdminInfoApiServiceImpl implements AdminInfoApiService {
    private final AdminInfoApiServiceFeign adminInfoApiServiceFeign;

    @Override
    public AdminUserVo getAdminById(Long id) {
        return adminInfoApiServiceFeign.getAdminById(id);
    }

    @Override
    public HashSet<String> getResourceKeys(Long id) {
        return adminInfoApiServiceFeign.getResourceKeys(id);
    }
}