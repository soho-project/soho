package work.soho.admin.common.security.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import work.soho.admin.common.security.userdetails.SohoUserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class SecurityUtils {
    /**
     * 默认token租期
     */
    private static Integer DEFAULT_TOKEN_LEASE_TERM = 3600 * 24 * 7;

    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    public SohoUserDetails getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (SohoUserDetails) authentication.getPrincipal();
    }

    /**
     * 获取当前登录的用户
     *
     * @return
     */
    public Long getLoginUserId() {
        try {
            return getLoginUser().getId();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 创建token
     *
     * @param loginUser
     * @param leaseTerm 租期
     * @param secret 加签密钥
     * @return
     */
    public String createToken(SohoUserDetails loginUser, Integer leaseTerm, String secret)
    {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", loginUser.getId());
        claims.put("uname", loginUser.getUsername());
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + leaseTerm))
                .claim("authorities", loginUser.getAuthorities())
                .signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }

    /**
     * 获取token相关信息
     *
     * @param loginUser
     * @param leaseTerm 租期
     * @param secret 加签密钥
     * @return
     */
    public Map<String, String> createTokenInfo(SohoUserDetails loginUser, Integer leaseTerm, String secret) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("token", createToken(loginUser, leaseTerm, secret));
        map.put("iat", String.valueOf(new Date().getTime()));
        map.put("exp", String.valueOf(new Date().getTime() + leaseTerm));
        return map;
    }
}
