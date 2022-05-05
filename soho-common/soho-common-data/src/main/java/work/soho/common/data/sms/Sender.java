package work.soho.common.data.sms;

import java.util.List;

/**
 * 短信发送接口
 */
public interface Sender {
    String sendSms(Message message) throws Exception;
    List<String> sendBatchSms(List<Message> messages);
    void loadProperties(String name);
}
