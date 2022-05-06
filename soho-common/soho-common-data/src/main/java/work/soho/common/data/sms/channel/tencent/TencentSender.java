package work.soho.common.data.sms.channel.tencent;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import work.soho.common.core.util.BinderUtil;
import work.soho.common.data.sms.Message;
import work.soho.common.data.sms.Sender;

import java.util.List;
import java.util.stream.Collectors;

public class TencentSender implements Sender {
    private TencentProperties tencentProperties;

    @Override
    public String sendSms(Message message) throws Exception {
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
            Credential cred = new Credential(tencentProperties.getSecretId(), tencentProperties.getSecretKey());
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(tencentProperties.getEndpoint());
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            SmsClient client = new SmsClient(cred, tencentProperties.getRegion(), clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            SendSmsRequest req = new SendSmsRequest();
            String[] phoneNumberSet1 = {message.getPhoneNumbers()};
            req.setPhoneNumberSet(phoneNumberSet1);
            req.setSmsSdkAppid(tencentProperties.getSdkAppid());
            req.setSign(message.getSignName());
            req.setTemplateID(message.getTemplateCode());
            String[] params =message.getParams().values().stream().collect(Collectors.toList()).toArray(new String[message.getParams().size()]);


            req.setTemplateParamSet(params);

            // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
            SendSmsResponse resp = client.SendSms(req);
            return resp.getSendStatusSet()[0].getSerialNo();
            // 输出json格式的字符串回包
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<String> sendBatchSms(List<Message> messages) {
        return null;
    }

    @Override
    public void loadProperties(String name) {
        setProperties(BinderUtil.bind(name, TencentProperties.class));
    }

    public void setProperties(TencentProperties tencentProperties) {
        this.tencentProperties = tencentProperties;
    }
}
