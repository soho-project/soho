package work.soho.admin.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

/**
 * 
 * @TableName admin_config
 */
@TableName(value ="admin_config")
@Data
@ToString
public class AdminConfig implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 配置文件分组名
     */
    @TableField(value = "group_key")
    private String groupKey;

    /**
     * 配置信息唯一识别key
     */
    @TableField(value = "`key`")
    private String key;

    /**
     * 配置信息值
     */
    @TableField(value = "value")
    private String value;

    @TableField(value = "`explain`")
    private String explain;

    /**
     * 配置信息类型
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedTime;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}