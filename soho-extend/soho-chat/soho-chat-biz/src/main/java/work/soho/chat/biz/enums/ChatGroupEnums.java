package work.soho.chat.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ChatGroupEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Type {
        GROUP_CHAT(2,"群聊"),
        GROUP_(3,"群组");
        private final int id;
        private final String name;
    }
}