package work.soho.chat.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import lombok.Data;
import java.time.LocalDateTime;

@TableName(value ="chat_user_friend_questions")
@Data
public class ChatUserFriendQuestions implements Serializable {
    /**
    * ID
    */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
    * 用户ID
    */
    @TableField(value = "uid")
    private Long uid;

    /**
    * 提问问题
    */
    @TableField(value = "questions")
    private String questions;

    /**
    * 问题答案
    */
    @TableField(value = "answer")
    private String answer;

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
