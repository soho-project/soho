package work.soho.common.data.sms.channel.aliyun;

import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import work.soho.common.core.util.BinderUtil;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.JacksonUtils;
import work.soho.common.data.sms.Message;
import work.soho.common.data.sms.Sender;

import java.util.ArrayList;
import java.util.List;

/**
 * 阿里云短信发送接口实现
 */
public class AliyunSender implements Sender {
    private AliyunProperties aliyunProperties;

    /**
     * 获取短信发送客户端
     *
     * @return
     * @throws Exception
     */
    private com.aliyun.dysmsapi20170525.Client createClient() throws Exception {
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(aliyunProperties.getAccessKeyId())
                // 您的AccessKey Secret
                .setAccessKeySecret(aliyunProperties.getAccessKeySecret());
        // 访问的域名
        config.endpoint = aliyunProperties.getEndpoint();
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    @Override
    public String sendSms(Message message) throws Exception {
        com.aliyun.dysmsapi20170525.Client client = createClient();
        SendSmsRequest sendSmsRequest = new SendSmsRequest();
        sendSmsRequest.setPhoneNumbers(message.getPhoneNumbers());
        sendSmsRequest.setSignName(message.getSignName());
        sendSmsRequest.setTemplateCode(message.getTemplateCode());
        sendSmsRequest.setTemplateParam(JacksonUtils.toJson(message.getParams()));
        sendSmsRequest.setOutId(String.valueOf(IDGeneratorUtils.snowflake().longValue()));
        SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
        return sendSmsResponse.getBody().getBizId();
    }

    @Override
    public List<String> sendBatchSms(List<Message> messages) {
        ArrayList<String> list = new ArrayList<>();
        messages.forEach(item->{
            try {
                list.add(sendSms(item));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return list;
    }

    @Override
    public void loadProperties(String name) {
        aliyunProperties = BinderUtil.bind(name, AliyunProperties.class);
    }

    /**
     * 设置配置信息
     *
     * @param aliyunProperties
     */
    public void setProperties(AliyunProperties aliyunProperties) {
        this.aliyunProperties = aliyunProperties;
    }
}
