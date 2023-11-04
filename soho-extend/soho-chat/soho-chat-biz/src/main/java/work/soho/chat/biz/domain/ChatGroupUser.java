package work.soho.chat.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import lombok.Data;
import java.time.LocalDateTime;

@TableName(value ="chat_group_user")
@Data
public class ChatGroupUser implements Serializable {
    /**
    * ID
    */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
    * 群组ID
    */
    @TableField(value = "group_id")
    private Long groupId;

    /**
    * 用户ID
    */
    @TableField(value = "chat_uid")
    private Long chatUid;

    /**
    * 是否管理员;0:否,1:是
    */
    @TableField(value = "is_admin")
    private Integer isAdmin;

    /**
    * 备注名
    */
    @TableField(value = "notes_name")
    private String notesName;

    /**
    * 更新时间
    */
    @TableField(value = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
    * 创建时间
    */
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
     * 群用户昵称
     */
    @TableField(value = "nickname")
    private String nickname;

    /**
     * 禁言最后时间
     */
    @TableField(value = "banned_end_time")
    private LocalDateTime bannedEndTime;

}
