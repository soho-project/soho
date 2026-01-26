package work.soho.content.biz.wordpress.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * WordPress 评论 DTO
 */
@Data
public class WordPressCommentDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("post")
    private Long post;

    @JsonProperty("parent")
    private Long parent;

    @JsonProperty("author")
    private Long author;

    @JsonProperty("author_name")
    private String authorName;

    @JsonProperty("author_email")
    private String authorEmail;

    @JsonProperty("author_url")
    private String authorUrl;

    @JsonProperty("date")
    private String date;

    @JsonProperty("date_gmt")
    private String dateGmt;

    @JsonProperty("content")
    private WordPressRendered content = new WordPressRendered();

    @JsonProperty("status")
    private String status;
}
