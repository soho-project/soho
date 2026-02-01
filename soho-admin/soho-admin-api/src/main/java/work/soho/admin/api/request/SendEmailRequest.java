package work.soho.admin.api.request;

import lombok.Data;

import java.util.HashMap;

@Data
public class SendEmailRequest {
    // 模版名称
    private String name;

    // 接收人
    private String to;

    // 接收的参数
    private HashMap<String, Object> params;
}
