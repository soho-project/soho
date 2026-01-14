package work.soho.open.biz.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName(value ="open_user")
public class OpenUser implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer id;

    /**
    * 用户ID
    */
    @ExcelProperty("用户ID")
    @ApiModelProperty(value = "用户ID")
    @TableField(value = "user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    /**
    * 用户名
    */
    @ExcelProperty("用户名")
    @ApiModelProperty(value = "用户名")
    @TableField(value = "username")
    private String username;

    /**
    * 登录密码
    */
    @ExcelProperty("登录密码")
    @ApiModelProperty(value = "登录密码")
    @TableField(value = "password")
    private String password;

    /**
    * 用户名
    */
    @ExcelProperty("用户名")
    @ApiModelProperty(value = "用户名")
    @TableField(value = "name")
    private String name;

    /**
    * 账号类型
    */
    @ExcelProperty("账号类型")
    @ApiModelProperty(value = "账号类型")
    @TableField(value = "type")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer type;

    /**
    * 联系人名
    */
    @ExcelProperty("联系人名")
    @ApiModelProperty(value = "联系人名")
    @TableField(value = "contact_name")
    private String contactName;

    /**
    * 联系人邮箱
    */
    @ExcelProperty("联系人邮箱")
    @ApiModelProperty(value = "联系人邮箱")
    @TableField(value = "contact_email")
    private String contactEmail;

    /**
    * 联系人手机
    */
    @ExcelProperty("联系人手机")
    @ApiModelProperty(value = "联系人手机")
    @TableField(value = "contact_phone")
    private String contactPhone;

    /**
    * 更新时间
    */
    @ExcelProperty("更新时间")
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
    * 创建时间
    */
    @ExcelProperty("创建时间")
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

}