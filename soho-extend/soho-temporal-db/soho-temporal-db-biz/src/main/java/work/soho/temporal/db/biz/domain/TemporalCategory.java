package work.soho.temporal.db.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import lombok.Data;
import java.time.LocalDateTime;

@TableName(value ="temporal_category")
@Data
public class TemporalCategory implements Serializable {
    /**
    * null
    */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * 分类名
    */
    @TableField(value = "name")
    private String name;

    /**
    * 分类标题
    */
    @TableField(value = "title")
    private String title;

    /**
    * 分类父ID;;foreign:temporal_category.id,frontType:treeSelect,undefined:undefined
    */
    @TableField(value = "parent_id")
    private Integer parentId;

    /**
    * 备注
    */
    @TableField(value = "notes")
    private String notes;

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

}