package work.soho.common.security.service;

import work.soho.common.security.userdetails.SohoUserDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * 角色自定义认证实现
 *
 * 默认使用的jwt认证; 已经实现无须实现该接口
 */
public interface SohoRoleAuthenticationService {
    SohoUserDetails getLoginUser(HttpServletRequest request);
}
