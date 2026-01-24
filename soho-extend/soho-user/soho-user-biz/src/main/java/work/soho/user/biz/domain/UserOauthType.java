package work.soho.user.biz.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName(value ="user_oauth_type")
@ApiModel("三方认证类型")
public class UserOauthType implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer id;

    /**
    * 名称
    */
    @ExcelProperty("名称")
    @ApiModelProperty(value = "名称")
    @TableField(value = "name")
    private String name;

    /**
    * 标题
    */
    @ExcelProperty("标题")
    @ApiModelProperty(value = "标题")
    @TableField(value = "title")
    private String title;

    /**
    * LOGO
    */
    @ExcelProperty("LOGO")
    @ApiModelProperty(value = "LOGO")
    @TableField(value = "logo")
    private String logo;

    /**
    * 客户端ID
    */
    @ExcelProperty("客户端ID")
    @ApiModelProperty(value = "客户端ID")
    @TableField(value = "client_id")
    private String clientId;

    /**
    * 客户端密钥
    */
    @ExcelProperty("客户端密钥")
    @ApiModelProperty(value = "客户端密钥")
    @TableField(value = "client_secret")
    private String clientSecret;

    /**
    * 状态
    */
    @ExcelProperty("状态")
    @ApiModelProperty(value = "状态")
    @TableField(value = "status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    /**
    * adapter
    */
    @ExcelProperty("adapter")
    @ApiModelProperty(value = "adapter")
    @TableField(value = "adapter")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer adapter;

    /**
    * updated_time
    */
    @ExcelProperty("updated_time")
    @ApiModelProperty(value = "updated_time")
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
    * 适配器
    */
    @ExcelProperty("适配器")
    @ApiModelProperty(value = "适配器")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

}
