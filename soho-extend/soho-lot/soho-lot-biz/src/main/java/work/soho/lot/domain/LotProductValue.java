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
 * 产品值
 * @TableName lot_product_value
 */
@TableName(value ="lot_product_value")
@Data
public class LotProductValue implements Serializable {
    /**
     * 
     */
    @TableField(value = "id")
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 模型单元ID
     */
    @TableField(value = "params_name")
    private String paramsName;

    /**
     * 产品ID
     */
    @TableField(value = "product_id")
    private Long productId;

    /**
     * 产品标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 产品当前实际值
     */
    @TableField(value = "value")
    private String value;

    /**
     * 产品设置值
     */
    @TableField(value = "given_value")
    private String givenValue;

    /**
     * 单位
     */
    @TableField(value = "unit")
    private String unit;

    /**
     * 值类型
     */
    @TableField(value = "type")
    private String type;

    /**
     * 排序
     */
    @TableField(value = "`order`")
    private Integer order;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}