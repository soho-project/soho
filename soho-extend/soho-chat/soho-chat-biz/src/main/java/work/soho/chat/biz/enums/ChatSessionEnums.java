package work.soho.chat.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ChatSessionEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Type {
        PRIVATE_CHAT(1,"私聊"),
        GROUP_CHAT(2,"群聊"),
        GROUP(3,"群组"),
        CUSTOMER_SERVICE(4,"客服"),
        SELF(5,"自己"),  //一般用来跨客户端通信

        ;
        private int id;
        private String name;

        Type(int i, String name) {
            this.id = i;
            this.name = name;
        }
    }

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        ACTIVE(1,"活跃"),
        DISABLED(2,"禁用"),
        DELETE(3,"删除");
        private int id;
        private String name;

        Status(int i, String name) {
            this.id = i;
            this.name = name;
        }
    }
}
