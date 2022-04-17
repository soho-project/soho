package work.soho.common.data.captcha.utils;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import lombok.experimental.UtilityClass;
import org.springframework.core.env.Environment;
import work.soho.common.core.support.SpringContextHolder;
import work.soho.common.core.util.IpUtils;
import work.soho.common.core.util.ResponseUtil;
import work.soho.common.data.captcha.storage.Session;
import work.soho.common.data.captcha.storage.StorageInterface;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@UtilityClass
public class CaptchaUtils {
    /**
     * 创建且发送验证码
     *
     * @param response
     * @return
     */
    public String createAndSend() throws IOException {
        byte[] captcha = null;
        HttpServletResponse response = ResponseUtil.getResponse();
        Environment environment = SpringContextHolder.getBean(Environment.class);
        DefaultKaptcha defaultKaptcha = SpringContextHolder.getBean(DefaultKaptcha.class);
        StorageInterface storageInterface = SpringContextHolder.getBean(Session.class);
        //识别跟踪ID
        String key =environment.getProperty("captcha.keyName");
        key = key == null ? "default-captcha" : key;
        key = IpUtils.getClientIp() + ":" + key;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String createText = null;
        try {
            // 将生成的验证码保存在session中
            createText = defaultKaptcha.createText();
            storageInterface.set(key, createText);
            BufferedImage bi = defaultKaptcha.createImage(createText);
            ImageIO.write(bi, "jpg", out);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return createText;
        }

        captcha = out.toByteArray();
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        ServletOutputStream sout = response.getOutputStream();
        sout.write(captcha);
        sout.flush();
        sout.close();
        return createText;
    }
}
