package work.soho.common.data.sms;

import org.springframework.core.env.Environment;
import work.soho.common.core.support.SpringContextHolder;
import work.soho.common.data.sms.channel.aliyun.AliyunSend;

import java.util.HashMap;

public class ChannelManager {
    private final static String CONFIG_PREFIX = "sms.channels.";

    private final static HashMap<String, Class<? extends Sender>> channelTypes = new HashMap<>();

    static {
        channelTypes.put("aliyun", AliyunSend.class);
        //TODO 支持其他通道
    }

    /**
     * 获取短信渠道
     *
     * @param name
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public Sender getChannelByName(String name) throws InstantiationException, IllegalAccessException {
        String configName = CONFIG_PREFIX + name;
        Environment environment = SpringContextHolder.getBean(Environment.class);
        String type = environment.getProperty(configName + "type");
        Sender send = channelTypes.get(type).newInstance();
        send.loadProperties(configName + "config");
        return send;
    }
}
