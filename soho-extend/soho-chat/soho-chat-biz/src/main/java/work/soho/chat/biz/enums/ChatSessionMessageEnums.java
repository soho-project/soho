package work.soho.chat.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ChatSessionMessageEnums {
    @RequiredArgsConstructor
    @Getter
    public enum IsDeleted {
        NO(0,"NO"),
        YES(1,"YES"),

        ;
        private int id;
        private String name;

        IsDeleted(int i, String name) {
            this.id = i;
            this.name = name;
        }
    }
}
