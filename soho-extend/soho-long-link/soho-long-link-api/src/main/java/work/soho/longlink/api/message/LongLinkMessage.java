package work.soho.longlink.api.message;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class LongLinkMessage {
    public static final String DEFAULT_NAMESPACE = "default";
    public static final String DEFAULT_TOPIC = "default";
    public static final String DEFAULT_TYPE = "message";

    /**
     * 业务命名空间，例如 chat / admin-notice
     */
    private String namespace;

    /**
     * 业务主题，例如 message / notify
     */
    private String topic;

    /**
     * 消息类型，例如 text / command / event
     */
    private String type;

    /**
     * 消息体（可为字符串或结构化对象）
     */
    private Object payload;

    /**
     * 追踪ID
     */
    private String traceId;

    /**
     * 扩展头信息
     */
    private Map<String, String> headers = new HashMap<>();
}
