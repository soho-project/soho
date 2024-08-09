package work.soho.open.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName(value ="open_code")
@Data
public class OpenCode implements Serializable {
    /**
    * ID
    */
    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
    * APP ID
    */
    @ApiModelProperty(value = "APP ID")
    @TableField(value = "app_id")
    private Integer appId;

    /**
    * 编号
    */
    @ApiModelProperty(value = "编号")
    @TableField(value = "code")
    private String code;

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

    /**
    * 是否登录
    */
    @ApiModelProperty(value = "是否登录")
    @TableField(value = "is_login")
    private Integer isLogin;

    /**
    * 用户ID
    */
    @ApiModelProperty(value = "用户ID")
    @TableField(value = "uid")
    private Long uid;

    /**
    * 原始用户ID
    */
    @ApiModelProperty(value = "原始用户ID")
    @TableField(value = "origin_uid")
    private Long originUid;
}
