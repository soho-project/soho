package work.soho.lot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 产品
 * @TableName lot_product
 */
@TableName(value ="lot_product")
@Data
public class LotProduct implements Serializable {
    /**
     * 
     */
    @TableField(value = "id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模型ID
     */
    @TableField(value = "model_id")
    private Integer modelId;

    /**
     * 设备mac地址
     */
    @TableField(value = "mac")
    private String mac;

    /**
     * 所属用户
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
     * 产品状态
     */
    @TableField(value = "status")
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}