package work.soho.common.security.filter;

import cn.hutool.json.JSONUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import work.soho.common.core.result.BaseErrorCode;
import work.soho.common.core.result.R;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * 资源受限处理句柄
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable
{
    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
    {
        try {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("utf8");
            response.getWriter().write(JSONUtil.toJsonStr(R.error(BaseErrorCode.SC_UNAUTHORIZED)));
        } catch (Exception exception) {
            e.printStackTrace();
        }
    }
}
