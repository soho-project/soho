package work.soho.chat.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@TableName(value ="chat_user_friend")
@Data
public class ChatUserFriend implements Serializable {
    /**
    * ID;;
    */
    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
    * 聊天用户ID;;
    */
    @ApiModelProperty(value = "聊天用户ID")
    @TableField(value = "chat_uid")
    private Long chatUid;

    /**
    * 好友用户ID;;
    */
    @ApiModelProperty(value = "好友用户ID")
    @TableField(value = "friend_uid")
    private Long friendUid;

    /**
    * 好友备注名;;
    */
    @ApiModelProperty(value = "好友备注名")
    @TableField(value = "notes_name")
    private String notesName;
}
