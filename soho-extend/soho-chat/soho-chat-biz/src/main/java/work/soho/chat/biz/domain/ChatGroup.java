package work.soho.chat.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import lombok.Data;
import java.time.LocalDateTime;

@TableName(value ="chat_group")
@Data
public class ChatGroup implements Serializable {
    /**
    * ID
    */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
    * 标题
    */
    @TableField(value = "title")
    private String title;

    /**
    * 群组类型;2:群聊,3:群组
    */
    @TableField(value = "type")
    private Integer type;

    /**
    * 主管理员
    */
    @TableField(value = "master_chat_uid")
    private Long masterChatUid;

    /**
    * 群聊头像
    */
    @TableField(value = "avatar")
    private String avatar;

    /**
    * 简介
    */
    @TableField(value = "introduction")
    private String introduction;

    /**
    * 群公告
    */
    @TableField(value = "proclamation")
    private String proclamation;

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

}
