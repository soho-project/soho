package work.soho.common.data.sms.utils;

import lombok.experimental.UtilityClass;
import work.soho.common.core.support.SpringContextHolder;
import work.soho.common.data.sms.ChannelManager;
import work.soho.common.data.sms.Message;

@UtilityClass
public class SmsUtils {
    /**
     * 短信发送
     *
     * 使用默认渠道发送短信
     *
     * @param message
     * @return
     * @throws Exception
     */
    public String sendSms(Message message) throws Exception {
        //get default channel
        String defaultChannel = SpringContextHolder.getProperty("sms.defaultChannel");
        return sendSms(defaultChannel, message);
    }

    /**
     * 指定渠道发送短信
     *
     * @param channelName
     * @param message
     * @return
     * @throws Exception
     */
    public String sendSms(String channelName, Message message) throws Exception {
        return new ChannelManager().getChannelByName(channelName).sendSms(message);
    }
}
