package work.soho.chat.api.payload;

import lombok.Data;

public class BaseType {
    @Data
    public static class User {
        private Long id;
        private String name;
        private String avatar = "https://gw.alicdn.com/tfs/TB1U7FBiAT2gK0jSZPcXXcKkpXa-108-108.jpg";
    }
}
