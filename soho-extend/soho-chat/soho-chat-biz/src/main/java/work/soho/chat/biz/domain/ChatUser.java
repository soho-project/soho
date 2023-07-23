package work.soho.chat.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import lombok.Data;
import java.time.LocalDateTime;

@TableName(value ="chat_user")
@Data
public class ChatUser implements Serializable {
    /**
    * 聊天用户ID
    */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * 用户名
    */
    @TableField(value = "username")
    private String username;

    /**
    * 用户昵称
    */
    @TableField(value = "nickname")
    private String nickname;

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
    * 用户原始类型；admin,user,supper等
    */
    @TableField(value = "origin_type")
    private String originType;

    /**
    * 原始用户ID;;
    */
    @TableField(value = "origin_id")
    private Integer originId;

}