package work.soho.admin.common.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import work.soho.admin.common.security.userdetails.SohoUserDetails;

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
