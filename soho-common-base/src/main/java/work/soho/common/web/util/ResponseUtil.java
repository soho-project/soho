package work.soho.common.web.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import work.soho.common.bean.result.R;
import work.soho.common.json.util.JacksonUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * <p>
 * ResponseUtil
 * </p>
 *
 * @author livk
 * @date 2021/11/2
 */
@UtilityClass
public class ResponseUtil {

    public HttpServletResponse getResponse() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        Assert.notNull(servletRequestAttributes, "attributes not null!");
        return servletRequestAttributes.getResponse();
    }

    public void out(String message) {
        HttpServletResponse response = ResponseUtil.getResponse();
        Assert.notNull(response, "response not null!");
        ResponseUtil.out(response, message);
    }

    /**
     * 根据response写入返回值
     *
     * @param response response
     * @param message  写入的信息
     */
    public void out(HttpServletResponse response, String message) {
        R<?> r = R.error(message);
        try (PrintWriter out = response.getWriter()) {
            out.print(JacksonUtils.toJson(r));
            out.flush();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}