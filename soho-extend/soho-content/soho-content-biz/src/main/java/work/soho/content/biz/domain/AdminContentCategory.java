package work.soho.content.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 内容分类
 * @TableName admin_content_category
 */
@TableName(value ="admin_content_category")
@Data
public class AdminContentCategory implements Serializable {
    /**
     * 分类ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 分类名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 父分类ID
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 分类描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 分类关键字
     */
    @TableField(value = "keyword")
    private String keyword;

    /**
     * 分类内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 排序
     */
    @TableField(value = "`order`")
    private Integer order;

    /**
     * 分类是否显示 0 不显示  1 显示
     */
    @TableField(value = "is_display")
    private Integer isDisplay;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    @TableField(value = "created_time")
    private LocalDateTime createdTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
