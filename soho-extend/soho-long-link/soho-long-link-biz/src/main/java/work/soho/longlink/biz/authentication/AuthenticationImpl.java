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
        if (secret == null || secret.trim().isEmpty() || "defaultValue".equals(secret)) {
            log.error("longlink auth secret is not configured");
            return null;
        }
        try {
            Claims claims = parseToken(token);
            return claims.get("uid", Integer.class).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Claims parseToken(String token)
    {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}
