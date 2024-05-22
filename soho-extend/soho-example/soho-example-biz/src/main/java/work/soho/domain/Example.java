package work.soho.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import lombok.Data;
import java.time.LocalDateTime;

@TableName(value ="example")
@Data
public class Example implements Serializable {
    /**
    * null
    */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * 标题
    */
    @TableField(value = "title")
    private String title;

    /**
    * 分类ID
    */
    @TableField(value = "category_id")
    private Integer categoryId;

    /**
    * 选项
    */
    @TableField(value = "option_id")
    private String optionId;

    /**
    * 富媒体
    */
    @TableField(value = "content")
    private String content;

    /**
    * null
    */
    @TableField(value = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
    * null
    */
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
    * 审批状态
    */
    @TableField(value = "apply_status")
    private Integer applyStatus;

    /**
    * 状态
    */
    @TableField(value = "status")
    private Integer status;

    /**
    * 用户
    */
    @TableField(value = "user_id")
    private Integer userId;

    /**
    * 开放用户
    */
    @TableField(value = "open_id")
    private Integer openId;

}