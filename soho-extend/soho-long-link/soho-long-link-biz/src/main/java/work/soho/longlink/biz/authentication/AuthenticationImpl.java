package work.soho.longlink.biz.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import work.soho.longlink.api.authentication.Authentication;

@Log4j2
@Component
public class AuthenticationImpl implements Authentication {
    @Value("${token.secret:defaultValue}")
    private String secret;

    @Override
    public String getUidWithToken(String token) {
        try {
            Claims claims = parseToken(token);
            log.info("auth claims: {}", claims);
            return claims.get("uid", Integer.class).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Claims parseToken(String token)
    {
        log.info("auth secret: {}， token: {}", secret, token);
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}
