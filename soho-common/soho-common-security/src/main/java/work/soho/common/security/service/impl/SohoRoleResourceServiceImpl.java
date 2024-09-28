package work.soho.common.security.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.common.security.service.SohoRoleResourceService;
import work.soho.common.security.service.SohoRoleUserResourceService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SohoRoleResourceServiceImpl implements SohoRoleResourceService {
    private final Map<String, SohoRoleUserResourceService> roleUserResourceServiceMap;

    /**
     * 角色用户资源服务前缀
     */
    private final static String SERVICE_ROLE_USER_RESOURCE_PREFIX = "role-user-resource-:";

    @Override
    public Boolean isExists(String roleName, Long userId, String key) {
        SohoRoleUserResourceService service = getService(roleName);
        return service != null && service.isExists(key, userId);
    }

    /**
     * 获取角色资源服务
     *
     * @param roleName
     * @return
     */
    private SohoRoleUserResourceService getService(String roleName) {
        return roleUserResourceServiceMap.get(SERVICE_ROLE_USER_RESOURCE_PREFIX + roleName);
    }
}
