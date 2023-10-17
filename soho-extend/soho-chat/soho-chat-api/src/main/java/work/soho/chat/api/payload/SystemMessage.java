package work.soho.chat.api.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SystemMessage {
    private String type = "system";
    private SystemMessage.Content content;

    @JsonProperty("_id")
    private String id;
    /**
     * 创建时间(毫秒)
     */
    private Long createdAt = System.currentTimeMillis();

    @Data
    public static class Content {
        private String text;
    }

    public static class Builder {
        private String text;
        private String id;

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public SystemMessage build() {
            SystemMessage system = new SystemMessage();
            if(id != null) {
                system.setId(id);
            }
            SystemMessage.Content content1 = new Content();
            content1.setText(text);
            system.setContent(content1);
            return system;
        }
    }
}
