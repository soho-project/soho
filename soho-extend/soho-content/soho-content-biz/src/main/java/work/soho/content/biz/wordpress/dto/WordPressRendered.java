package work.soho.content.biz.wordpress.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * WordPress 渲染字段 DTO
 */
@Data
public class WordPressRendered {
    @JsonProperty("rendered")
    private String rendered;

    @JsonProperty("protected")
    private boolean protectedContent;
}
