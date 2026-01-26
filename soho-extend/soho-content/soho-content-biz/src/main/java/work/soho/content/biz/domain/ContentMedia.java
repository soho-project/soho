package work.soho.content.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 内容媒体资源实体
 */
@TableName(value = "content_media")
@Data
public class ContentMedia implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 外部媒体ID
     */
    @TableField(value = "external_media_id")
    private Long externalMediaId;

    /**
     * 标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 资源URL
     */
    @TableField(value = "url")
    private String url;

    /**
     * 媒体类型
     */
    @TableField(value = "mime_type")
    private String mimeType;

    /**
     * 描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 替代文本
     */
    @TableField(value = "alt_text")
    private String altText;

    /**
     * 作者用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 创建时间
     */
    @TableField(value = "created_time")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time")
    private LocalDateTime updatedTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
