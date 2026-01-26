package work.soho.content.biz.wordpress.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * WordPress 媒体 DTO
 */
@Data
public class WordPressMediaDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("date")
    private String date;

    @JsonProperty("date_gmt")
    private String dateGmt;

    @JsonProperty("slug")
    private String slug;

    @JsonProperty("type")
    private String type;

    @JsonProperty("link")
    private String link;

    @JsonProperty("title")
    private WordPressRendered title = new WordPressRendered();

    @JsonProperty("caption")
    private WordPressRendered caption = new WordPressRendered();

    @JsonProperty("alt_text")
    private String altText;

    @JsonProperty("media_type")
    private String mediaType;

    @JsonProperty("mime_type")
    private String mimeType;

    @JsonProperty("source_url")
    private String sourceUrl;

    @JsonProperty("author")
    private Long author;
}
