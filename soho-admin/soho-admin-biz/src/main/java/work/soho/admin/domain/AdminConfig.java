package work.soho.admin.domain;

import java.io.Serializable;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 系统配置表
 *
 * @TableName admin_config
 */
@Data
public class AdminConfig implements Serializable {


    /**
     * ID
     */
    @ApiModelProperty("ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 配置文件分组名
     */
    @ApiModelProperty("配置文件分组名")
    private String groupKey;

    /**
     * 配置信息唯一识别key
     */
    @ApiModelProperty("配置信息唯一识别key")
    @TableField(value = "`key`")
    private String key;

    /**
     * 配置信息值
     */
    @ApiModelProperty("配置信息值")
    private String value;

    /**
     * 说明
     */
    @ApiModelProperty("说明")
    @TableField(value = "`explain`")
    private String explain;

    /**
     * 配置信息类型
     */
    @ApiModelProperty("配置信息类型")
    private Integer type;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

}
