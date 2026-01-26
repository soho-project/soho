package work.soho.content.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 外部内容映射实体
 */
@TableName(value = "content_external_mapping")
@Data
public class ContentExternalMapping implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 内容ID
     */
    @TableField(value = "content_id")
    private Long contentId;

    /**
     * 外部对象ID
     */
    @TableField(value = "external_object_id")
    private Long externalObjectId;

    /**
     * 外部类型
     */
    @TableField(value = "external_type")
    private String externalType;

    /**
     * 外部别名
     */
    @TableField(value = "external_slug")
    private String externalSlug;

    /**
     * 外部状态
     */
    @TableField(value = "external_status")
    private String externalStatus;

    /**
     * 外部访问密码
     */
    @TableField(value = "external_password")
    private String externalPassword;

    /**
     * 外部评论状态
     */
    @TableField(value = "external_comment_status")
    private String externalCommentStatus;

    /**
     * 外部Ping状态
     */
    @TableField(value = "external_ping_status")
    private String externalPingStatus;

    /**
     * 外部排序
     */
    @TableField(value = "external_menu_order")
    private Integer externalMenuOrder;

    /**
     * 外部父级ID
     */
    @TableField(value = "external_parent_id")
    private Long externalParentId;

    /**
     * 外部特色媒体ID
     */
    @TableField(value = "external_featured_media_id")
    private Long externalFeaturedMediaId;

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
