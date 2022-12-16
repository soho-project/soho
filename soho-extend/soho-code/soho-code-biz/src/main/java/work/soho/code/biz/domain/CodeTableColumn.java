package work.soho.code.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 代码表字段
 * @TableName code_table_column
 */
@TableName(value ="code_table_column")
@Data
public class CodeTableColumn implements Serializable {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 表ID;;frontType:option,foreign:code_table.id~title
     */
    @TableField(value = "table_id")
    private Integer tableId;

    /**
     * 表名
     */
    @TableField(value = "name")
    private String name;

    /**
     * 表标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 数据类型
     */
    @TableField(value = "data_type")
    private String dataType;

    /**
     * 是否主键;0:否,1:是;frontType:select
     */
    @TableField(value = "is_pk")
    private Integer isPk;

    /**
     * 是否不为空;0:否,1:是;frontType:select
     */
    @TableField(value = "is_not_null")
    private Integer isNotNull;

    /**
     * 是否无符号;0:否,1:是;frontType:select
     */
    @TableField(value = "is_unique")
    private Integer isUnique;

    /**
     * 是否自增;0:否,1:是;frontType:select
     */
    @TableField(value = "is_auto_increment")
    private Integer isAutoIncrement;

    /**
     * 是否0填充;0:否,1:是;frontType:select
     */
    @TableField(value = "is_zero_fill")
    private Integer isZeroFill;

    /**
     * 默认值
     */
    @TableField(value = "default_value")
    private String defaultValue;

    /**
     * 长度
     */
    @TableField(value = "length")
    private Integer length;

    /**
     * 字段注释
     */
    @TableField(value = "comment")
    private String comment;

    /**
     * 小数点位数
     */
    @TableField(value = "scale")
    private Integer scale;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
