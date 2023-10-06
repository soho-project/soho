package work.soho.chat.api.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import work.soho.common.core.util.IDGeneratorUtils;

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

    public static class Builder {
        private String avatar;
        private String contentName;
        private Integer contentSize;
        private String contentUrl;
        private String position;

        public Builder() {}

        public Builder userAvatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public Builder contentName(String name) {
            this.contentName = name;
            return this;
        }

        public Builder contentSize(Integer size) {
            this.contentSize = size;
            return this;
        }

        public Builder contentUrl(String url) {
            this.contentUrl = url;
            return this;
        }

        public Builder position(String position) {
            this.position = position;
            return this;
        }

        public File build() {
            File text1 = new File();
            if(avatar != null) {
                User user1 = new User();
                user1.setAvatar(avatar);
                text1.setUser(user1);
            }
            Content content1 = new Content();
            content1.setName(contentName);
            content1.setUrl(contentUrl);
            content1.setSize(contentSize);
            text1.setContent(content1);
            if(position != null) {
                text1.setPosition(position);
            }
            text1.setId(IDGeneratorUtils.snowflake().toString());
            return text1;
        }
    }
}
