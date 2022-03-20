package work.soho.admin.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import work.soho.admin.domain.AdminUser;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenServiceImpl {
    private final static String HEADER = "Authorization";

    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    // 令牌秘钥
    @Value("${token.secret:defaultValue}")
    private String secret;

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public UserDetailsServiceImpl.UserDetailsImpl getLoginUser(HttpServletRequest request)
    {
        // 获取请求携带的令牌
        String token = getToken(request);
        if (StringUtils.isNotEmpty(token))
        {
            Claims claims = parseToken(token);
            UserDetailsServiceImpl.UserDetailsImpl user = new UserDetailsServiceImpl.UserDetailsImpl();
            user.setId((String) claims.get("uid"));
            user.setUsername((String) claims.get("uname"));
            return user;
        }
        return null;
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token)
    {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 创建token
     *
     * @param loginUser
     * @return
     */
    public String createToken(UserDetailsServiceImpl.UserDetailsImpl loginUser)
    {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", loginUser.getId());
        claims.put("uname", loginUser.getUsername());
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }

    /**
     * 获取请求token
     *
     * @param request
     * @return token
     */
    private String getToken(HttpServletRequest request)
    {
        String token = request.getHeader(HEADER);
        if (StringUtils.isNotEmpty(token) && token.startsWith(TOKEN_PREFIX))
        {
            token = token.replace(TOKEN_PREFIX, "");
        }
        return token;
    }
}
