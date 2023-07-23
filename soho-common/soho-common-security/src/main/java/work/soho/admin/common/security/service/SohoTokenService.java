package work.soho.admin.common.security.service;

import work.soho.admin.common.security.userdetails.SohoUserDetails;

import java.util.Map;

public interface SohoTokenService {

    /**
     * 获取登录用户
     *
     * @return
     */
    SohoUserDetails getLoginUser();

    /**
     * 创建token
     *
     * @param loginUser
     * @return
     */
    String createToken(SohoUserDetails loginUser);

    /**
     * 创建Token信息
     *
     * @param loginUser
     * @return
     */
    Map<String, String> createTokenInfo(SohoUserDetails loginUser);
}
