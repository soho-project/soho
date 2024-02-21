package work.soho.mobile.verification.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.http.HttpServletRequest;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;
import work.soho.api.admin.service.AdminConfigApiService;
import work.soho.mobile.verification.annotations.RequireValidToken;
import java.util.InvalidPropertiesFormatException;

@Component
@Aspect
public class AndroidApiAspect {
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private AdminConfigApiService adminConfigApiService;

    @Before("@annotation(requireValidToken)")
    public void validateIP(RequireValidToken requireValidToken) throws InvalidPropertiesFormatException {
        String token = request.getHeader("Authentication-Info");
        String localToken = getValue(requireValidToken.token());
        if (!localToken.equals(token)) {
            throw new InvalidPropertiesFormatException("Token 错误，鉴权失败");
        }
    }

    private String getValue(String expression) {
        return adminConfigApiService.getByKey(expression, String.class, "");
    }
}
