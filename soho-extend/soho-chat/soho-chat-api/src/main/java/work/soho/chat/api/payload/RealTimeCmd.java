package work.soho.chat.api.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.HashMap;
import java.util.UUID;

@Data
public class RealTimeCmd {
    @JsonProperty("_id")
    private String id;
    private String type = "realTimeCmd";
    private RealTimeCmd.Content content;

    @Data
    public static class Content {
        private String name;
        private HashMap<String, Object> params;
    }

    public static RealTimeCmdBuilder builder() {
        RealTimeCmdBuilder realTimeCmdBuilder = new RealTimeCmdBuilder();
        return realTimeCmdBuilder;
    }

    public static class RealTimeCmdBuilder {
        private String name;
        private HashMap<String, Object> params;

        public RealTimeCmdBuilder() {

        }
        public RealTimeCmdBuilder(String name, HashMap<String, Object> params) {
            this.name = name;
            this.params = params;
        }

        public RealTimeCmdBuilder params(HashMap<String, Object> p) {
            params = p;
            return this;
        }

        public RealTimeCmdBuilder name(String n) {
            name = n;
            return this;
        }

        /**
         * 创建 RealTimeCmd 指令
         *
         * @return
         */
        public RealTimeCmd build() {
            RealTimeCmd realTimeCmd = new RealTimeCmd();
            realTimeCmd.setId(UUID.randomUUID().toString());
            Content content1 = new Content();
            content1.setName(name);
            content1.setParams(params);
            realTimeCmd.setContent(content1);
            return realTimeCmd;
        }
    }
}
