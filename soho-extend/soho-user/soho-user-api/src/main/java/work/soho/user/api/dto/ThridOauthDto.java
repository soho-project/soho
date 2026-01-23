package work.soho.user.api.dto;

import lombok.Data;

@Data
public class ThridOauthDto {
    /**
     * 平台ID
     */
    private Integer platformId;

    /**
     * 第三方app登录唯一标识
     */
    private String openId;

    /**
     * 第三方登录唯全平台一标识
     */
    private String unionId;

    /**
     * 第三方登录用户名
     */
    private String username;

    /**
     * 第三方登录昵称
     */
    private String nickname;

    /**
     * 第三方登录头像
     */
    private String avatar;

    /**
     * 第三方登录性别
     *
     * 1:男 2:女 0:未知
     */
    private Integer gender;

    /**
     * 第三方登录手机号
     */
    private String phone;

    /**
     * 第三方登录访问令牌
     */
    private String accessToken;

    /**
     * 第三方登录访问令牌有效期
     */
    private Integer expireIn;

    /**
     * 第三方登录刷新令牌
     */
    private String refreshToken;

    /**
     * 第三方登录刷新令牌有效期
     */
    private Integer refreshTokenExpireIn;
}
