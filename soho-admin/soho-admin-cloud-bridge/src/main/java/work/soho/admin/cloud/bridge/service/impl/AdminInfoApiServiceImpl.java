package work.soho.admin.cloud.bridge.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.admin.cloud.bridge.feign.AdminInfoApiServiceFeign;
import work.soho.admin.api.service.AdminInfoApiService;
import work.soho.admin.api.vo.AdminUserVo;
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