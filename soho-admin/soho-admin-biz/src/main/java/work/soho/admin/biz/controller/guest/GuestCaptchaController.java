package work.soho.admin.biz.controller.guest;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.data.captcha.utils.CaptchaUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 管理后台访客验证码控制器。
 */
@Slf4j
@RestController
@RequestMapping("/admin/guest/auth")
@Api(tags = "管理后台访客验证码")
public class GuestCaptchaController {

    /**
     * 生成并输出验证码图片。
     *
     * @param response 响应对象
     * @throws IOException IO 异常
     */
    @GetMapping("/captcha")
    public void captcha(HttpServletResponse response) throws IOException {
        try {
            CaptchaUtils.createAndSend();
        } catch (Exception e) {
            log.error("生成验证码失败", e);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
