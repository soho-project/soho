package work.soho.admin.api.service;

import java.util.List;

/**
 * 资源接口
 *
 * 改接口的服务名格式务必为："role-user-resource-admin"　　其中　admin 为角色名，前面为固定前缀
 */
public interface AdminResourceApiService {
    /**
     * 根据角色名获取资源Key列表
     *
     * @param roleName
     * @return
     */
    List<String> getResourceKeyListByRole(String roleName);
}
