package work.soho.chat.biz.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class UpdateGroupAuthReq {
    /**
     * 群ID
     */
    @ApiModelProperty(value = "群ID")
    private Long groupId;

    /**
     * 认证方式
     */
    @ApiModelProperty(value = "认证方式")
    private Integer authType;

    /**
     * 认证问题列表
     */
    @ApiModelProperty(value = "认证问题列表")
    private List<Questions> questionsList;

    @Data
    public static class Questions {
        /**
         * 问题
         */
        @ApiModelProperty(value = "问题")
        private String questions;

        /**
         * 答案
         */
        @ApiModelProperty(value = "答案")
        private String answer;
    }
}
