package work.soho.code.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 代码表
 * @TableName code_table
 */
@TableName(value ="code_table")
@Data
public class CodeTable implements Serializable {
    /**
     * ID
     */
    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 表名
     */
    @ApiModelProperty(value = "表名")
    @TableField(value = "name")
    private String name;

    /**
     * 表标题
     */
    @ApiModelProperty(value = "表标题")
    @TableField(value = "title")
    private String title;

    /**
     * 表注释
     */
    @ApiModelProperty(value = "表注释")
    @TableField(value = "comment")
    private String comment;

    /**
     * 所属数据源
     */
    @ApiModelProperty(value = "数据源")
    @TableField(value = "db_source")
    private String dbSource;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
