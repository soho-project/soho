package work.soho.common.security.service;

public interface SohoRoleResourceService {
    /**
     * 检查指定角色某用户是否拥有某权限
     *
     * @param roleName
     * @param userId
     * @param key
     * @return
     */
    Boolean isExists(String roleName, Long userId, String key);
}
