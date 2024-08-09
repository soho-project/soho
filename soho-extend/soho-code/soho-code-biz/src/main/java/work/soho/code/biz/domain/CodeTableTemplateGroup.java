package work.soho.code.biz.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 模本分组;;option:id~name
 * @TableName code_table_template_group
 */
@TableName(value ="code_table_template_group")
@Data
public class CodeTableTemplateGroup implements Serializable {
    /**
     * ID
     */
    @ApiModelProperty("ID")
    @TableId(value = "id")
    private Integer id;

    /**
     * 标题
     */
    @ApiModelProperty("标题")
    @TableField(value = "title")
    private String title;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    @TableField(value = "name")
    private String name;

    /**
     * 基础路径
     */
    @ApiModelProperty("基础路径")
    @TableField(value = "base_path")
    private String basePath;

    /**
     * 该组入口函数
     */
    @ApiModelProperty("该组入口函数")
    @TableField(value = "main_function")
    private String mainFunction;

    /**
     * 说明
     */
    @ApiModelProperty("说明")
    @TableField(value = "`explain`")
    private String explain;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    @TableField(value = "updated_time")
    private LocalDateTime updatedTime;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @TableField(value = "created_time")
    private LocalDateTime createdTime;

    /**
     * 排序
     */
    @ApiModelProperty("排序")
    @TableField(value = "sort")
    private Integer sort;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
