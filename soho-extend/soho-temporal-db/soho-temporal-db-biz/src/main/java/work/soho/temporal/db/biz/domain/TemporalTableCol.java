package work.soho.temporal.db.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import lombok.Data;
import java.time.LocalDateTime;

@TableName(value ="temporal_table_col")
@Data
public class TemporalTableCol implements Serializable {
    /**
    * ID
    */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * 名称
    */
    @TableField(value = "name")
    private String name;

    /**
    * 标题
    */
    @TableField(value = "title")
    private String title;

    /**
    * 表ID;;foreign:temporal_table.id~title,undefined:undefined
    */
    @TableField(value = "table_id")
    private Integer tableId;

    /**
    * 数据类型
    */
    @TableField(value = "type")
    private Integer type;

    /**
    * null
    */
    @TableField(value = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
    * null
    */
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
    * 状态;0:禁用,1:活跃
    */
    @TableField(value = "status")
    private Integer status;

}