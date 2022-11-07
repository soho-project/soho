package work.soho.admin.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
//import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import work.soho.admin.domain.AdminUser;
import work.soho.common.core.util.StringUtils;

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
    {// 获取请求携带的令牌
        String token = getToken(request);
        if (StringUtils.isNotEmpty(token))
        {
            Claims claims = parseToken(token);
            UserDetailsServiceImpl.UserDetailsImpl user = new UserDetailsServiceImpl.UserDetailsImpl();
            user.setId(Long.valueOf(claims.get("uid").toString()));
            user.setUsername((String) claims.get("uname"));
            return user;
        }
        return null;
    }

    /**
     * 获取当前登录的用户
     *
     * @return
     */
    public UserDetailsServiceImpl.UserDetailsImpl getLoginUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return getLoginUser(request);
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
                .setExpiration(new Date(new Date().getTime() + getTokenLeaseTerm()))
                .claim("authorities", loginUser.getAuthorities())
                .signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }

    /**
     * 获取token相关信息
     *
     * @param loginUser
     * @return
     */
    public Map<String, String> createTokenInfo(UserDetailsServiceImpl.UserDetailsImpl loginUser) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("token", createToken(loginUser));
        map.put("iat", String.valueOf(new Date().getTime()));
        map.put("exp", String.valueOf(new Date().getTime() + getTokenLeaseTerm()));
        return map;
    }

    /**
     * 获取token租期
     *
     * @return
     */
    private Long getTokenLeaseTerm() {
        return 3600l * 365 * 1000;
    }

    /**
     * 获取请求token
     *
     * @param request
     * @return token
     */
    public String getToken(HttpServletRequest request)
    {
        String token = request.getHeader(HEADER);
        if (StringUtils.isNotEmpty(token) && token.startsWith(TOKEN_PREFIX))
        {
            token = token.replace(TOKEN_PREFIX, "");
        }
        return token;
    }
}
