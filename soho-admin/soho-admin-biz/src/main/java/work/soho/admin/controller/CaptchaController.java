package work.soho.admin.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.data.captcha.utils.CaptchaUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RestController
@RequiredArgsConstructor
public class CaptchaController {
    private final static Logger logger = LoggerFactory.getLogger(CaptchaController.class);

    @GetMapping("/captcha")
    public void defaultKaptcha(HttpServletResponse response) throws IOException {
        try {
            CaptchaUtils.createAndSend();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
    }
}
