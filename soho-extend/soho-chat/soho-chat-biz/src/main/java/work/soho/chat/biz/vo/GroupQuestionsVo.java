package work.soho.chat.biz.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GroupQuestionsVo {
        /**
         * 认证方式
         */
        private Integer authType;

        /**
         * 认证问题列表
         */
        private List<Questions> questionsList = new ArrayList<>();

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
