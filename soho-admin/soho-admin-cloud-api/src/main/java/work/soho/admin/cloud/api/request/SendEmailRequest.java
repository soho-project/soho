package work.soho.admin.cloud.api.request;

import lombok.Data;

import java.util.Map;

@Data
public class SendEmailRequest {
    private String to;
    private String name;
    private Map<String, Object> model;
    private Map<String, String> files;
}
