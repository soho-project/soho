package work.soho.admin.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName(value ="admin_email_template")
@Data
public class AdminEmailTemplate implements Serializable {
    /**
    * ID
    */
    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * 模板名;;isFilter:true
    */
    @ApiModelProperty(value = "模板名;;isFilter:true")
    @TableField(value = "name")
    private String name;

    /**
    * 邮件标题;;isFilter:true
    */
    @ApiModelProperty(value = "邮件标题;;isFilter:true")
    @TableField(value = "title")
    private String title;

    /**
    * 邮件内容;;isFilter:true
    */
    @ApiModelProperty(value = "邮件内容;;isFilter:true")
    @TableField(value = "body")
    private String body;

    /**
    * 更新时间
    */
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
    * 创建时间
    */
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

}
