package work.soho.admin.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName(value ="admin_operation_log")
@Data
public class AdminOperationLog implements Serializable {
    /**
    * ID
    */
    @ApiModelProperty(value = "ID")
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
    * 管理员;;frontType:select,foreign:admin_user.id~username,isFilter:true
    */
    @ApiModelProperty(value = "管理员;;frontType:select,foreign:admin_user.id~username,isFilter:true")
    @TableField(value = "admin_user_id")
    private Long adminUserId;

    /**
    * 请求方法;GET:GET,POST:POST,DELETE:DELETE,PUT:PUT;frontType:select,
    */
    @ApiModelProperty(value = "请求方法;GET:GET,POST:POST,DELETE:DELETE,PUT:PUT;frontType:select,")
    @TableField(value = "method")
    private String method;

    /**
    * 请求路径
    */
    @ApiModelProperty(value = "请求路径")
    @TableField(value = "path")
    private String path;

    /**
    * 请求参数
    */
    @ApiModelProperty(value = "请求参数")
    @TableField(value = "params")
    private String params;

    /**
    * 操作内容;;frontType:text,isFilter:true
    */
    @ApiModelProperty(value = "操作内容;;")
    @TableField(value = "content")
    private String content;

    /**
    * 返回数据
    */
    @ApiModelProperty(value = "返回数据")
    @TableField(value = "response")
    private String response;

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