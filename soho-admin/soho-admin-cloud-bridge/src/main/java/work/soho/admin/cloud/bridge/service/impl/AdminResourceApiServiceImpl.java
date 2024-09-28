package work.soho.admin.cloud.bridge.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.admin.api.service.AdminResourceApiService;
import work.soho.admin.cloud.bridge.feign.AdminResourceApiServiceFeign;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminResourceApiServiceImpl implements AdminResourceApiService {
    private final AdminResourceApiServiceFeign adminResourceApiServiceFeign;

    @Override
    public List<String> getResourceKeyListByRole(String roleName) {
        return adminResourceApiServiceFeign.getResourceKeyListByRole(roleName);
    }
}
