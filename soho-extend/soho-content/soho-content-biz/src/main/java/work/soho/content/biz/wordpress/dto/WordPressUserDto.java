package work.soho.content.biz.wordpress.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * WordPress 用户 DTO
 */
@Data
public class WordPressUserDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("slug")
    private String slug;

    @JsonProperty("description")
    private String description;

    @JsonProperty("url")
    private String url;

    @JsonProperty("link")
    private String link;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;
}
