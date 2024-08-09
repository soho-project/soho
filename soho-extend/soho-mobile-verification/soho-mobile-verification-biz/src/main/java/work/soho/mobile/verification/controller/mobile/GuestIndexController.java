package work.soho.mobile.verification.controller.mobile;

import cn.hutool.core.lang.Assert;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.result.R;
import work.soho.mobile.verification.api.service.VerificationServiceApi;
import work.soho.mobile.verification.api.vo.VerificationCodeVo;
import work.soho.mobile.verification.config.SohoMobileVerificationConfig;
import work.soho.mobile.verification.service.VerificationService;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Random;

@Api(tags = "客户端手机认证API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/guest/api/mobile")
public class GuestIndexController {
    private final VerificationService verificationService;
    private final VerificationServiceApi verificationServiceApi;
    private final SohoMobileVerificationConfig sohoConfig;

    /**
     * 检查指定验证状态
     *
     * @param id
     * @return
     */
    @GetMapping("isSuccess")
    public R<Boolean> isSuccess(String id) {
        try {
            VerificationCodeVo verificationCodeVo = verificationServiceApi.getById(id);
            Assert.notNull(verificationCodeVo, "认证超时！");
            return R.success(verificationCodeVo.getIsSuccess());
        } catch (InvalidParameterException e) {
            return R.error(e.getMessage());
        } catch (Exception e) {
            return R.error("系统错误");
        }
    }

    /**
     * 根据ID获取认证二维码内容
     *
     * @param id
     * @return
     */
    @GetMapping("getCodeById")
    public R<String> showCodeById(String id) {
        try {
            ArrayList<String> numbers = sohoConfig.getPhoneNumbers();
            Assert.notNull(numbers, "当前没有配置接收短信手机号");
            Assert.equals(numbers.size(), 0, "当前没有配置接收短信手机号");
            // 创建一个随机数生成器
            Random random = new Random();
            // 生成一个随机索引
            int randomIndex = random.nextInt(numbers.size());
            // 使用随机索引从ArrayList中获取数据
            String randomPhoneNumber = numbers.get(randomIndex);

            VerificationCodeVo verificationCodeVo = verificationServiceApi.getById(id);
            Assert.notNull(verificationCodeVo, "认证超时！");
            String body = "SMSTO:"+randomPhoneNumber+":" + verificationCodeVo.getId();
            return R.success(body);
        } catch (IllegalArgumentException e) {
            return R.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("系统错误");
        }
    }

    /**
     * 创建认证二维码
     *
     * @return
     */
    @GetMapping("createCode")
    public R<String> createCode() {
        try {
            String id = verificationServiceApi.createVerification();
            ArrayList<String> numbers = sohoConfig.getPhoneNumbers();
            Assert.notNull(numbers, "当前没有配置接收短信手机号");
            Assert.notEquals(numbers.size(), 0, "当前没有配置接收短信手机号");
            // 创建一个随机数生成器
            Random random = new Random();
            // 生成一个随机索引
            int randomIndex = random.nextInt(numbers.size());
            // 使用随机索引从ArrayList中获取数据
            String randomPhoneNumber = numbers.get(randomIndex);
            String body = "SMSTO:"+randomPhoneNumber+":" + id;
            return R.success(body);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("系统错误");
        }
    }
}
