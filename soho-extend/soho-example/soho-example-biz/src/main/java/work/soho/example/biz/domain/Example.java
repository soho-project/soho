package work.soho.example.biz.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName(value ="example")
@ApiModel("自动化样例")
public class Example implements Serializable {
    /**
    * ID
    */
    @ExcelProperty("ID")
    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer id;

    /**
    * 标题
    */
    @ExcelProperty("标题")
    @ApiModelProperty(value = "标题")
    @TableField(value = "title")
    private String title;

    /**
    * 分类ID
    */
    @ExcelProperty("分类ID")
    @ApiModelProperty(value = "分类ID")
    @TableField(value = "category_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer categoryId;

    /**
    * 选项
    */
    @ExcelProperty("选项")
    @ApiModelProperty(value = "选项")
    @TableField(value = "option_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer optionId;

    /**
    * 富媒体
    */
    @ExcelProperty("富媒体")
    @ApiModelProperty(value = "富媒体")
    @TableField(value = "content")
    private String content;

    /**
    * 更新时间
    */
    @ExcelProperty("更新时间")
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
    * 创建时间
    */
    @ExcelProperty("创建时间")
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
    * 状态
    */
    @ExcelProperty("状态")
    @ApiModelProperty(value = "状态")
    @TableField(value = "status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    /**
    * 审批状态
    */
    @ExcelProperty("审批状态")
    @ApiModelProperty(value = "审批状态")
    @TableField(value = "apply_status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer applyStatus;

    /**
    * 用户
    */
    @ExcelProperty("用户")
    @ApiModelProperty(value = "用户")
    @TableField(value = "user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    /**
    * 开放用户
    */
    @ExcelProperty("开放用户")
    @ApiModelProperty(value = "开放用户")
    @TableField(value = "open_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer openId;

    /**
    * 管理员ID
    */
    @ExcelProperty("管理员ID")
    @ApiModelProperty(value = "管理员ID")
    @TableField(value = "admin_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer adminId;

    /**
    * 字典整型
    */
    @ExcelProperty("字典整型")
    @ApiModelProperty(value = "字典整型")
    @TableField(value = "dict_int")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer dictInt;

    /**
    * 字符串字典
    */
    @ExcelProperty("字符串字典")
    @ApiModelProperty(value = "字符串字典")
    @TableField(value = "dict_string")
    private String dictString;

}