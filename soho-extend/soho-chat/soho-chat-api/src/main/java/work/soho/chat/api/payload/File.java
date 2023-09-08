package work.soho.chat.api.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class File {
    private String type = "file";
    private File.Content content;
    private File.User user;

    private String position;

    @JsonProperty("_id")
    private String id;

    @Data
    public static class Content {
        private String name;
        private Integer size;
        private String url;
    }

    @Data
    public static class User {
        private String avatar = "https://gw.alicdn.com/tfs/TB1U7FBiAT2gK0jSZPcXXcKkpXa-108-108.jpg";
    }
}
