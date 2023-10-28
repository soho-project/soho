package work.soho.chat.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ChatUserEnums {

    /**
     * 用户被添加好友认证方式
     */
    @RequiredArgsConstructor
    @Getter
    public enum AuthFriendType {
        ANY_ONE(1,"允许任何人"),
        NEED_RECOGNITION(2,"需要验证信息"),
        ANSWER_QUESTIONS(3,"需要回答问题"),
        ANSWER_QUESTIONS_RECOGNITION(4,"需要回答问题并由我确认"),
        REFUSE(5, "禁止加我好友");

        private final int type;
        private final String name;
    }
}
