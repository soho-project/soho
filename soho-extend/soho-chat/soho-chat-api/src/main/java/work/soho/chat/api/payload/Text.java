package work.soho.chat.api.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Text {
    private String type = "text";
    private Content content;
    private User user;

    private String position;

    @JsonProperty("_id")
    private String id;

    @Data
    public static class Content {
        private String text;
    }

    @Data
    public static class User {
        private String avatar = "https://gw.alicdn.com/tfs/TB1U7FBiAT2gK0jSZPcXXcKkpXa-108-108.jpg";
    }
}
