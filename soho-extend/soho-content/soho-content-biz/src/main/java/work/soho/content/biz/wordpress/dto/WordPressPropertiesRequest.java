package work.soho.content.biz.wordpress.dto;

import lombok.Data;

/**
 * 前端传入的 WordPress 连接配置
 */
@Data
public class WordPressPropertiesRequest {
    /**
     * 站点根地址，例如：https://example.com
     */
    private String baseUrl;
    /**
     * 用户名（可选，用于 Basic Auth）
     */
    private String username;
    /**
     * 应用密码（可选，用于 Basic Auth）
     */
    private String appPassword;
}

