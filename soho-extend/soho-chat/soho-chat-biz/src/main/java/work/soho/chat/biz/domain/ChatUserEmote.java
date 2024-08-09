package work.soho.chat.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName(value ="chat_user_emote")
@Data
public class ChatUserEmote implements Serializable {
    /**
    * ID
    */
    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
    * 用户ID
    */
    @ApiModelProperty(value = "用户ID")
    @TableField(value = "uid")
    private Long uid;

    /**
    * 表情URL地址
    */
    @ApiModelProperty(value = "表情URL地址")
    @TableField(value = "url")
    private String url;

    /**
    * 创建时间
    */
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
     * 表情备注
     */
    @ApiModelProperty(value = "备注")
    @TableField(value = "notes")
    private String notes;
}
