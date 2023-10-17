package work.soho.chat.api.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import work.soho.common.core.util.IDGeneratorUtils;

@Data
public class Image extends BaseType {
    private String type = "image";
    private Image.Content content;
    private Image.User user;

    private String position;

    @JsonProperty("_id")
    private String id;
    /**
     * 创建时间(毫秒)
     */
    private Long createdAt = System.currentTimeMillis();

    @Data
    public static class Content {
        private String picUrl;
    }

    public static class Builder {
        private String avatar;
        private String picUrl;
        private String position;

        public Builder() {}

        public Builder userAvatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public Builder contentPicUrl(String url) {
            this.picUrl = picUrl;
            return this;
        }

        public Builder position(String position) {
            this.position = position;
            return this;
        }

        public Image build() {
            Image text1 = new Image();
            if(avatar != null) {
                User user1 = new User();
                user1.setAvatar(avatar);
                text1.setUser(user1);
            }
            Content content1 = new Content();
            content1.setPicUrl(picUrl);
            text1.setContent(content1);
            if(position != null) {
                text1.setPosition(position);
            }
            text1.setId(IDGeneratorUtils.snowflake().toString());
            return text1;
        }
    }
}

