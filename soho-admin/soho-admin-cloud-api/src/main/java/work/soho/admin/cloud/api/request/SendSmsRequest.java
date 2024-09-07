package work.soho.admin.cloud.api.request;

import lombok.Data;

import java.util.Map;

@Data
public class SendSmsRequest {
    private String phone;
    private String name;
    private Map<String, String> model;
}
