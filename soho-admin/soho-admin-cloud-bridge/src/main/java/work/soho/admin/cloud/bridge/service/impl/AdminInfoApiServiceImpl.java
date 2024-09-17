package work.soho.admin.cloud.bridge.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.admin.cloud.bridge.feign.AdminInfoApiServiceFeign;
import work.soho.admin.api.service.AdminInfoApiService;
import work.soho.admin.api.vo.AdminUserVo;
import work.soho.common.security.service.SohoRoleUserResourceService;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class AdminInfoApiServiceImpl implements AdminInfoApiService, SohoRoleUserResourceService {
    private final AdminInfoApiServiceFeign adminInfoApiServiceFeign;

    @Override
    public AdminUserVo getAdminById(Long id) {
        return adminInfoApiServiceFeign.getAdminById(id);
    }

    @Override
    public HashSet<String> getResourceKeys(Long id) {
        return adminInfoApiServiceFeign.getResourceKeys(id);
    }

    /**
     * 检查指定用户是否拥有指定权限
     *
     * @param key
     * @param userId
     * @return
     */
    @Override
    public Boolean isExists(String key, Long userId) {
        if(userId == 1) {
            return Boolean.TRUE;
        }
        return getResourceKeys(userId).contains(key);
    }
}