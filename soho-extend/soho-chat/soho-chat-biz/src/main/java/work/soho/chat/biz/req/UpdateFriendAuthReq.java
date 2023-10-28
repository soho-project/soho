package work.soho.chat.biz.req;

import lombok.Data;

import java.util.List;

@Data
public class UpdateFriendAuthReq {
    /**
     * 认证方式
     */
    private Integer authType;

    /**
     * 认证问题列表
     */
    private List<Questions> questionsList;

    @Data
    public static class Questions {
        /**
         * 问题
         */
        private String questions;

        /**
         * 答案
         */
        private String answer;
    }
}
