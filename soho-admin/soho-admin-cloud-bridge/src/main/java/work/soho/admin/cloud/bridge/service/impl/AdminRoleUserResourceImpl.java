package work.soho.admin.cloud.bridge.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.admin.api.service.AdminInfoApiService;
import work.soho.common.security.service.SohoRoleUserResourceService;

import java.util.HashSet;

/**
 * 角色用户资源实现
 */
@Service
@RequiredArgsConstructor
public class AdminRoleUserResourceImpl implements SohoRoleUserResourceService {
    private final AdminInfoApiService adminInfoApiService;

    @Override
//    @Cacheable(name = "role_user_resource_exists", key = "#key + '_' + #userId")
    public Boolean isExists(String key, Long userId) {
        if(userId == 1) {
            return Boolean.TRUE;
        }
        HashSet<String> keys = adminInfoApiService.getResourceKeys(userId);
        return keys.contains(key);
    }
}
