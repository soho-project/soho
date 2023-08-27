package work.soho.chat.biz.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FriendApplyVO {
    /**
     * null
     */
    private Long id;

    /**
     * null
     */
    private Long chatUid;

    /**
     * null
     */
    private Long friendUid;

    /**
     * 申请状态;0:待处理,1:已同意,2:已拒绝
     */
    private Integer status;

    /**
     * 提问的问题
     */
    private String ask;

    /**
     * 回答答案
     */
    private String answer;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
     * 目标用户
     * 申请人或者被申请人
     */
    private BaseUserVO targetUser;
}
