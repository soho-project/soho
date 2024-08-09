package work.soho.temporal.db.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName(value ="temporal_table")
@Data
public class TemporalTable implements Serializable {
    /**
    * ID
    */
    @ApiModelProperty("ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * 名称
    */
    @ApiModelProperty("名称")
    @TableField(value = "name")
    private String name;

    /**
    * 标题
    */
    @ApiModelProperty("标题")
    @TableField(value = "title")
    private String title;

    /**
    * 更新时间
    */
    @ApiModelProperty("更新时间")
    @TableField(value = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
    * 创建时间
    */
    @ApiModelProperty("创建时间")
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
    * 分类ID;;frontType:treeSelect,foreign:temporal_category.id~title,frontName:时序分类
    */
    @ApiModelProperty("分类ID;;")
    @TableField(value = "category_id")
    private Integer categoryId;
}