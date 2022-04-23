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
 * 配置分组表
 *
 * @TableName admin_config_group
 */
@Data
public class AdminConfigGroup implements Serializable {


    /**
     * ID
     */
    @ApiModelProperty("ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * group key
     */
    @ApiModelProperty("组识别键")
    @TableField(value = "`key`")
    private String key;

    /**
     * 组名
     */
    @ApiModelProperty("组名")
    private String name;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

}
