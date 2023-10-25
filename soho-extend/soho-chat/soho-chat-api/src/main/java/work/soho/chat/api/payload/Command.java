package work.soho.chat.api.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;

@Data
public class Command extends BaseType implements PayloadBaseInterface {
    @JsonProperty("_id")
    private String id;
    private String type = "command";
    private Command.Content content;
    private User user;
    private String position;
    /**
     * 创建时间(毫秒)
     */
    private Long createdAt = System.currentTimeMillis();

    @Data
    public static class Content {
        private String name;
        private HashMap<String, Object> params;
    }

    public static class Builder {
        private String userName;
        private String userAvatar;
        private String name;
        private HashMap<String, Object> params;

        public Builder userAvatar(String avatar) {
            this.userAvatar = avatar;
            return this;
        }

        public Builder userName(String name) {
            this.userName = name;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder params(HashMap<String, Object> params) {
            this.params = params;
            return this;
        }

        /**
         * build
         *
         * @return
         */
        public Command build() {
            Command command = new Command();
            if(userAvatar != null) {
                User user1 = new User();
                user1.setAvatar(userAvatar);
                user1.setName(userName);
                command.setUser(user1);
            }
            Content content1 = new Content();
            content1.setName(name);
            content1.setParams(params);
            command.setContent(content1);

            return command;
        }
    }
}
