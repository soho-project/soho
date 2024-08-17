package work.soho.example.biz.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName(value ="example")
@Data
public class Example implements Serializable {
    /**
    * ID
    */
    @ExcelProperty("ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * 标题
    */
    @ExcelProperty("标题")
    @TableField(value = "title")
    private String title;

    /**
    * 分类ID
    */
    @ExcelProperty("分类ID")
    @TableField(value = "category_id")
    private Integer categoryId;

    /**
    * 选项
    */
    @ExcelProperty("选项")
    @TableField(value = "option_id")
    private String optionId;

    /**
    * 富媒体
    */
    @ExcelProperty("富媒体")
    @TableField(value = "content")
    private String content;

    /**
    * 更新时间
    */
    @ExcelProperty("更新时间")
    @TableField(value = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
    * 创建时间
    */
    @ExcelProperty("创建时间")
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
    * 审批状态
    */
    @ExcelProperty("申请状态")
    @TableField(value = "apply_status")
    private Integer applyStatus;

    /**
    * 状态
    */
    @ExcelProperty("状态")
    @TableField(value = "status")
    private Integer status;

    /**
    * 用户
    */
    @ExcelProperty("用户ID")
    @TableField(value = "user_id")
    private Integer userId;

    /**
    * 开放用户
    */
    @ExcelProperty("开放用户ID")
    @TableField(value = "open_id")
    private Integer openId;

}