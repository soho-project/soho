package work.soho.chat.api.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Image {
    private String type = "image";
    private Image.Content content;
    private Image.User user;

    private String position;

    @JsonProperty("_id")
    private String id;

    @Data
    public static class Content {
        private String picUrl;
    }

    @Data
    public static class User {
        private String avatar = "https://gw.alicdn.com/tfs/TB1U7FBiAT2gK0jSZPcXXcKkpXa-108-108.jpg";
    }
}

