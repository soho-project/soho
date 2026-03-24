package work.soho.ai.biz.dto;

import lombok.Data;

import java.util.List;

@Data
public class AiUserModelView {
    private Long providerConfigId;
    private String providerCode;
    private String provider;
    private String defaultModel;
    private List<String> models;
}
