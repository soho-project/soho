package work.soho.common.security.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import work.soho.common.security.service.SohoTokenService;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.common.core.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TokenServiceImpl implements SohoTokenService {
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
    public SohoUserDetails getLoginUser(HttpServletRequest request)
    {
        // 获取请求携带的令牌
        String token = getToken(request);
        if (StringUtils.isNotEmpty(token))
        {
            Claims claims = parseToken(token);
            SohoUserDetails user = new SohoUserDetails();
            user.setId(Long.valueOf(claims.get("uid").toString()));
            user.setUsername((String) claims.get("uname"));
            //从token还原认证角色信息
            List<String> authoritiesList = ((ArrayList<?>) claims.get("authorities"))
                    .stream()
                    .flatMap(item -> ((Map<?, ?>) item).values().stream())
                    .map(Object::toString)
                    .collect(Collectors.toList());
            user.setAuthorities(AuthorityUtils.createAuthorityList(authoritiesList.toArray(new String[0])));
            return user;
        }
        return null;
    }

    /**
     * 获取当前登录的用户
     *
     * @return
     */
    public SohoUserDetails getLoginUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return getLoginUser(request);
    }

    @Override
    public String createToken(SohoUserDetails loginUser) {
        return createToken(loginUser, new HashMap<>());
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
    public String createToken(SohoUserDetails loginUser,HashMap<String, Object> params)
    {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", loginUser.getId());
        claims.put("uname", loginUser.getUsername());
        if(params != null) {
            claims.putAll(params);
        }
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
    public Map<String, String> createTokenInfo(SohoUserDetails loginUser) {
        return createTokenInfo(loginUser, new HashMap<>());
    }

    /**
     * 创建token信息
     *
     * @param loginUser
     * @param params
     * @return
     */
    public Map<String, String> createTokenInfo(SohoUserDetails loginUser,HashMap<String,Object> params) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("token", createToken(loginUser, params));
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
