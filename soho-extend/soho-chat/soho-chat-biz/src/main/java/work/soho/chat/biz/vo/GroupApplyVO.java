package work.soho.chat.biz.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupApplyVO {
    /**
     * null
     */
    private Long id;

    /**
     * 申请用户ID
     */
    private Long chatUid;

    /**
     * 群聊ID
     */
    private Long groupId;

    /**
     * 申请状态;0:待处理,1:同意,2:拒绝
     */
    private Integer status;

    /**
     * 问题
     */
    private String ask;

    /**
     * 回答
     */
    private String answer;

    /**
     * null
     */
    private String applyMessage;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
     * 审批消息
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;


    /**
     * 目标用户
     * 申请人或者被申请人
     */
    private BaseUserVO targetUser;

    /**
     * 处理用户信息
     */
    private BaseUserVO doUser;

    /**
     * 群基本信息
     */
    private GroupVO groupVO;
}
