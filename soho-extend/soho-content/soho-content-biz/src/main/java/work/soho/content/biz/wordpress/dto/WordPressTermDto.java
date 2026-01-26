package work.soho.content.biz.wordpress.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * WordPress 分类/标签 DTO
 */
@Data
public class WordPressTermDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("count")
    private Integer count;

    @JsonProperty("description")
    private String description;

    @JsonProperty("link")
    private String link;

    @JsonProperty("name")
    private String name;

    @JsonProperty("slug")
    private String slug;

    @JsonProperty("taxonomy")
    private String taxonomy;

    @JsonProperty("parent")
    private Long parent;
}
