package work.soho.code.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 代码表模板
 * @TableName code_table_template
 */
@TableName(value ="code_table_template")
@Data
public class CodeTableTemplate implements Serializable {
    /**
     * ID
     */
    @ApiModelProperty("ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("名称")
    @TableField(value = "name")
    private String name;

    /**
     * 分组ID
     */
    @ApiModelProperty("分组ID")
    @TableField(value = "group_id")
    private Integer groupId;

    /**
     * 标题
     */
    @ApiModelProperty("标题")
    @TableField(value = "title")
    private String title;

    /**
     * 状态;0:禁用,1:活跃;forntType:select
     */
    @ApiModelProperty("状态;0:禁用,1:活跃;")
    @TableField(value = "status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    /**
     * 代码
     */
    @ApiModelProperty("代码")
    @TableField(value = "code")
    private String code;

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
