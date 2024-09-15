package work.soho.common.data.sms.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.admin.biz.AdminApplication;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.data.sms.Message;

import java.util.HashMap;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = AdminApplication.class)
class SmsUtilsTest {
    /**
     * 腾讯通道短信发送测试
     *
     * @throws Exception
     */
    @Test
    public void testSendSmsByName() throws Exception {
        //tencent
        HashMap<String, String> map  = new HashMap<>();
        map.put("code", "2222");
        Message message = new Message();
        message.setSignName("青春无极限")
                .setPhoneNumbers("+8615873164073")
                .setTemplateCode("1392711")
                .setOutId(String.valueOf(IDGeneratorUtils.snowflake().longValue()))
                .setParams(map);
        String outId = SmsUtils.sendSms("tencent", message);
        System.out.println("短信发送，外部单号：" + outId);
    }

    /**
     * 默认通道短信发送
     *
     * @throws Exception
     */
    @Test
    public void testAliyunSms() throws Exception {
        HashMap<String, String> map  = new HashMap<>();
        map.put("code", "2222");
        Message message = new Message();
        message.setSignName("igogo推")
                .setPhoneNumbers("15873164073")
                .setTemplateCode("SMS_39315103")
                .setOutId(String.valueOf(IDGeneratorUtils.snowflake().longValue()))
                .setParams(map);
        String outId = SmsUtils.sendSms( message);
        System.out.println("短信发送，外部单号：" + outId);
    }
}