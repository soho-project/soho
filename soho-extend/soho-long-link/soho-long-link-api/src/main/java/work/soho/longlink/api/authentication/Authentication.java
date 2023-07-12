package work.soho.longlink.api.authentication;

/**
 * 长链接认证接口
 */
public interface Authentication {
    String getUidWithToken(String token);
}
