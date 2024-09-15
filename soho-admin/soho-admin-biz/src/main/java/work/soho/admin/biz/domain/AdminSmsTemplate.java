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

@TableName(value ="admin_sms_template")
@Data
public class AdminSmsTemplate implements Serializable {
    /**
    * ID
    */
    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * 设配器名称;aliyun:aliyun,tencent:tencent;isFilter:true,frontType:select,
    */
    @ApiModelProperty(value = "设配器名称;")
    @TableField(value = "adapter_name")
    private String adapterName;

    /**
    * 代码中使用名称;;isFilter:true
    */
    @ApiModelProperty(value = "代码中使用名称;")
    @TableField(value = "name")
    private String name;

    /**
    * 标题;;isFilter:true
    */
    @ApiModelProperty(value = "标题;")
    @TableField(value = "title")
    private String title;

    /**
    * 模板编号;;isFilter:true
    */
    @ApiModelProperty(value = "模板编号;")
    @TableField(value = "template_code")
    private String templateCode;

    /**
    * 签名;;isFilter:true
    */
    @ApiModelProperty(value = "签名;")
    @TableField(value = "sign_name")
    private String signName;

    /**
    * 创建时间
    */
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
    * 更新时间
    */
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;
}