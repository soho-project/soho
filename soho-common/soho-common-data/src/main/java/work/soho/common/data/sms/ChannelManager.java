package work.soho.common.data.sms;

import org.joda.time.IllegalInstantException;
import org.springframework.core.env.Environment;
import work.soho.common.core.support.SpringContextHolder;
import work.soho.common.data.sms.channel.aliyun.AliyunSender;
import work.soho.common.data.sms.channel.tencent.TencentSender;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class ChannelManager {
    private final static String CONFIG_PREFIX = "sms.channels.";
    private final static HashMap<String, Class<? extends Sender>> channelTypes = new HashMap<>();

    private final static String TYPE_TENCENT = "tencent";
    private final static String TYPE_ALIYUN = "aliyun";

    static {
        //配置支持的短信渠道
        channelTypes.put(TYPE_ALIYUN, AliyunSender.class);
        channelTypes.put(TYPE_TENCENT, TencentSender.class);
    }

    /**
     * 获取短信渠道
     *
     * @param name
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public Sender getChannelByName(String name) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String configName = CONFIG_PREFIX + name;
        Environment environment = SpringContextHolder.getBean(Environment.class);
        String type = environment.getProperty(configName + ".type");
        //检查支持驱动是否包含该类型
        if(channelTypes.get(type) == null) {
            throw new IllegalInstantException("请传递正确的短信发送通道信息； 配置项错误：" + configName + ".type");
        }
        Sender send = channelTypes.get(type).getDeclaredConstructor().newInstance();
        send.loadProperties(configName + ".config");
        return send;
    }
}
