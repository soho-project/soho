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
 * 
 * @TableName lot_model_item
 */
@TableName(value ="lot_model_item")
@Data
public class LotModelItem implements Serializable {
    /**
     * 
     */
    @TableField(value = "id")
    private Integer id;

    /**
     * 模型ID
     */
    @TableField(value = "model_id")
    private Integer modelId;

    /**
     * 标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 单位
     */
    @TableField(value = "unit")
    private String unit;

    /**
     * 参数名
     */
    @TableField(value = "params_name")
    private String paramsName;

    /**
     * 提示
     */
    @TableField(value = "tips")
    private String tips;

    /**
     * wifi模块pin编号
     */
    @TableField(value = "extended_data")
    private String extendedData;

    /**
     * 数据类型
     */
    @TableField(value = "type")
    private String type;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
     * 创建时间
     */
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}