package work.soho.admin.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import lombok.Data;
import java.time.LocalDateTime;

@TableName(value ="admin_sms_template")
@Data
public class AdminSmsTemplate implements Serializable {
    /**
    * null
    */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * 设配器名称;aliyun:aliyun,tencent:tencent;isFilter:true,frontType:select,
    */
    @TableField(value = "adapter_name")
    private String adapterName;

    /**
    * 代码中使用名称;;isFilter:true
    */
    @TableField(value = "name")
    private String name;

    /**
    * 标题;;isFilter:true
    */
    @TableField(value = "title")
    private String title;

    /**
    * 模板编号;;isFilter:true
    */
    @TableField(value = "template_code")
    private String templateCode;

    /**
    * 签名;;isFilter:true
    */
    @TableField(value = "sign_name")
    private String signName;

    /**
    * 创建时间
    */
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
    * 更新时间
    */
    @TableField(value = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

}