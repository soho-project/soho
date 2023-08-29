package work.soho.chat.biz.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChatGroupVO {
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 群组类型;2:群聊,3:群组
     */
    private Integer type;

    /**
     * 主管理员
     */
    private Long masterChatUid;

    /**
     * 群聊头像
     */
    private String avatar;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 群公告
     */
    private String proclamation;

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
     * 群用户信息
     */
    private List<BaseUserVO> userList;

    /**
     * 群备注名
     */
    private String notesName;

    /**
     * 群用户昵称
     */
    private String nickname;
}
