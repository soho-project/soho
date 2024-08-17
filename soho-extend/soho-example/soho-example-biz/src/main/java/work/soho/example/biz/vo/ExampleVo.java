package work.soho.example.biz.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ExampleVo {
    /**
     * 标题
     */
    @ExcelProperty("标题")
    private String title;

    /**
     * 分类ID
     */
    @ExcelProperty("分类ID")
    private Integer categoryId;

    /**
     * 选项
     */
    @ExcelProperty("选项")
    private String optionId;

    /**
     * 富媒体
     */
    @ExcelProperty("富媒体")
    private String content;

    /**
     * 审批状态
     */
    @ExcelProperty("申请状态")
    private Integer applyStatus;

    /**
     * 状态
     */
    @ExcelProperty("状态")
    private Integer status;

    /**
     * 用户
     */
    @ExcelProperty("用户ID")
    private Integer userId;

    /**
     * 开放用户
     */
    @ExcelProperty("开放用户ID")
    private Integer openId;
}
