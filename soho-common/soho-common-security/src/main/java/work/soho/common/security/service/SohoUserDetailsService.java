package work.soho.common.security.service;

import work.soho.common.security.userdetails.SohoUserDetails;

/**
 * soho系统用户角色提供接口
 */
public interface SohoUserDetailsService {
    /**
     * 获取用户角色
     *
     * @param username
     * @return
     */
    SohoUserDetails loadUserByUsername(String username);

    /**
     * 获取用户角色
     *
     * @return
     */
    String getUserRoleName();
}
