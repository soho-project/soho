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

    @Getter
    @RequiredArgsConstructor
    public enum AuthJoinType {
        ANYONE(1, "允许任何人加群"),
        NEED_RECOGNITION(2,"需要验证信息"),
        ANSWER_QUESTIONS(3,"需要正确回答问题"),
        ANSWER_QUESTIONS_RECOGNITION(4,"需要回答问题并由我确认"),
        REFUSE(5, "禁止加群");

        private final int id;
        private final String name;
    }
}
