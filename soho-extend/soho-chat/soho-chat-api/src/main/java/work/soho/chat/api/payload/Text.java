package work.soho.chat.api.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import work.soho.common.core.util.IDGeneratorUtils;

@Data
public class Text extends BaseType implements PayloadBaseInterface {
    private String type = "text";
    private Content content;
    private User user;

    private String position;

    @JsonProperty("_id")
    private String id;
    /**
     * 创建时间(毫秒)
     */
    private Long createdAt = System.currentTimeMillis();

    /**
     * 引用回复
     */
    public static class Reply {
        @JsonProperty("_id")
        private String id;
        private User user;
        private String body;
    }

    @Data
    public static class Content {
        private String text;
        private Reply reply;
    }

    public static class Builder {
        private String avatar;
        private String text;
        private String position;

        public Builder() {}

        public Builder userAvatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public Builder contentText(String text) {
            this.text = text;
            return this;
        }

        public Builder position(String position) {
            this.position = position;
            return this;
        }

        public Text build() {
            Text text1 = new Text();
            if(avatar != null) {
                User user1 = new User();
                user1.setAvatar(avatar);
                text1.setUser(user1);
            }
            Content content1 = new Content();
            content1.setText(text);
            text1.setContent(content1);
            if(position != null) {
                text1.setPosition(position);
            }
            text1.setId(IDGeneratorUtils.snowflake().toString());
            return text1;
        }
    }
}
