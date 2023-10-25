package work.soho.chat.api.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoPhone extends BaseType implements PayloadBaseInterface{
    @JsonProperty("_id")
    private String id;
    private String type = "videoPhone";
    private Content content;
    private User user;
    private String position;
    /**
     * 创建时间(毫秒)
     */
    private Long createdAt = System.currentTimeMillis();
    @Data
    public static class Content {
        private String signal;

        /**
         * 电话类型 1 视频电话； 2 语音电话
         */
        private Integer phoneType;
    }

    /**
     * build
     */
    public static class Builder {
        private String avatar;
        private String text;
        private String position;
        private String signal;

        /**
         * 电话类型
         */
        private Integer phoneType = 1;

        public Builder() {
        }

        public VideoPhone.Builder userAvatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public VideoPhone.Builder contentText(String text) {
            this.text = text;
            return this;
        }

        public VideoPhone build() {
            VideoPhone video = new VideoPhone();
            if(avatar != null) {
                User user1 = new User();
                user1.setAvatar(avatar);
                video.setUser(user1);
            }
            Content content1 = new Content();
            content1.setSignal(signal);
            video.setContent(content1);
            return video;
        }
    }
}
