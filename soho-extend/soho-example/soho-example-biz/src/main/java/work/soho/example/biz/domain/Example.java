package work.soho.example.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import work.soho.common.data.excel.annotation.ExcelColumn;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName(value ="example")
@Data
public class Example implements Serializable {
    /**
    * ID
    */
    @ExcelColumn("ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * 标题
    */
    @ExcelColumn("标题")
    @TableField(value = "title")
    private String title;

    /**
    * 分类ID
    */
    @ExcelColumn("分类ID")
    @TableField(value = "category_id")
    private Integer categoryId;

    /**
    * 选项
    */
    @ExcelColumn("选项")
    @TableField(value = "option_id")
    private String optionId;

    /**
    * 富媒体
    */
    @ExcelColumn("富媒体")
    @TableField(value = "content")
    private String content;

    /**
    * 更新时间
    */
    @ExcelColumn("更新时间")
    @TableField(value = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
    * 创建时间
    */
    @ExcelColumn("创建时间")
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
    * 审批状态
    */
    @ExcelColumn("申请状态")
    @TableField(value = "apply_status")
    private Integer applyStatus;

    /**
    * 状态
    */
    @ExcelColumn("状态")
    @TableField(value = "status")
    private Integer status;

    /**
    * 用户
    */
    @ExcelColumn("用户ID")
    @TableField(value = "user_id")
    private Integer userId;

    /**
    * 开放用户
    */
    @ExcelColumn("开放用户ID")
    @TableField(value = "open_id")
    private Integer openId;

}