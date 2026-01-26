package work.soho.content.biz.wordpress.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WordPress 文章/页面 DTO
 */
@Data
public class WordPressPostDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("date")
    private String date;

    @JsonProperty("date_gmt")
    private String dateGmt;

    @JsonProperty("modified")
    private String modified;

    @JsonProperty("modified_gmt")
    private String modifiedGmt;

    @JsonProperty("slug")
    private String slug;

    @JsonProperty("status")
    private String status;

    @JsonProperty("type")
    private String type;

    @JsonProperty("link")
    private String link;

    @JsonProperty("title")
    private WordPressRendered title = new WordPressRendered();

    @JsonProperty("content")
    private WordPressRendered content = new WordPressRendered();

    @JsonProperty("excerpt")
    private WordPressRendered excerpt = new WordPressRendered();

    @JsonProperty("author")
    private Long author;

    @JsonProperty("featured_media")
    private Long featuredMedia;

    @JsonProperty("comment_status")
    private String commentStatus;

    @JsonProperty("ping_status")
    private String pingStatus;

    @JsonProperty("sticky")
    private boolean sticky;

    @JsonProperty("template")
    private String template;

    @JsonProperty("format")
    private String format;

    @JsonProperty("categories")
    private List<Long> categories = new ArrayList<>();

    @JsonProperty("tags")
    private List<Long> tags = new ArrayList<>();

    @JsonProperty("meta")
    private Map<String, Object> meta = new HashMap<>();
}
