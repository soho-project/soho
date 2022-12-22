package work.soho.code.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 代码表
 * @TableName code_table
 */
@TableName(value ="code_table")
@Data
public class CodeTable implements Serializable {
    /**
     *
     */
     @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

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
     * 表注释
     */
    @TableField(value = "comment")
    private String comment;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
