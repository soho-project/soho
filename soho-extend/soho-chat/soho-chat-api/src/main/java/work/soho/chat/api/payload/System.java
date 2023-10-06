package work.soho.chat.api.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class System {
    private String type = "system";
    private System.Content content;

    @JsonProperty("_id")
    private String id;

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

        public System build() {
            System system = new System();
            if(id != null) {
                system.setId(id);
            }
            System.Content content1 = new Content();
            content1.setText(text);
            system.setContent(content1);
            return system;
        }
    }
}
