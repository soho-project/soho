package work.soho.common.data.sms;

import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.admin.biz.AdminApplication;
import work.soho.common.core.util.IDGeneratorUtils;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = AdminApplication.class)
class MessageTest {
    @Autowired
    private Environment environment;

    @Value("${upload.channels.oss.config.accessKeyId}")
    private String accessKeyId;

    @Value("${upload.channels.oss.config.accessKeySecret}")
    private String accessKeySecret;

    public static com.aliyun.dysmsapi20170525.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    @Test
    void send() throws Exception {
        com.aliyun.dysmsapi20170525.Client client = createClient(accessKeyId, accessKeySecret);
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setSignName("igogo推")
                .setTemplateCode("SMS_39315103")
                .setPhoneNumbers("15873164073");

        //sendSmsRequest.setTemplateParam("{\"code\":\"1234\"}");
        sendSmsRequest.setOutId(String.valueOf(IDGeneratorUtils.snowflake().longValue()));
        SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
        System.out.println(sendSmsResponse.getBody().getMessage());
    }
}