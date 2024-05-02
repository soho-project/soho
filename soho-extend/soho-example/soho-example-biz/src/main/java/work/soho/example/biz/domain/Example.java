package work.soho.example.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 自动化样例表
 * @TableName example
 */
@TableName(value ="example")
@Data
public class Example implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 标题
     */
    private String title;

    /**
     * 状态；0:待审核,1:已通过,2:已拒绝
     */
    private Integer status;

    /**
     * 申请状态
     */
    private Integer applyStatus;

    /**
     * 分类ID;;frontType:treeSelect,foreign:example_category.id~title
     */
    private Integer categoryId;

    /**
     * 支付方式ID;;frontType:select,foreign:pay_info.id~title
     */
    private Integer optionId;

    /**
     * 富媒体;;frontType:editor
     */
    private String content;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
