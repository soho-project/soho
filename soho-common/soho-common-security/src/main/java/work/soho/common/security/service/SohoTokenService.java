package work.soho.common.security.service;

import work.soho.common.security.userdetails.SohoUserDetails;

import java.util.Map;

public interface SohoTokenService {

    /**
     * 获取登录用户
     *
     * @return
     */
    SohoUserDetails getLoginUser();


    /**
     * 通过JWT获取用户信息
     *
     * @param token
     * @return
     */
    SohoUserDetails getUserDetailsByJwtToken(String token);

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
