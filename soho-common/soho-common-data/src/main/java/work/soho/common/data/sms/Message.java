package work.soho.common.data.sms;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;

@Data
@Accessors(chain = true)
public class Message {
    /**
     * 接收短信手机号
     */
    private String phoneNumbers;

    /**
     * 短信签名
     */
    private String signName;

    /**
     * 短信模板ID
     */
    private String templateCode;

    /**
     * 外部短信跟踪号
     *
     * 一般为系统短信识别跟踪ID
     */
    private String outId;

    private HashMap<String, String> params;
}
