package work.soho.admin.api.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class AdminContentVo {
    /**
     * 分类导航
     */
    private List<NavItem> navs = new ArrayList<>();

    /**
     * 作者用户名
     */
    private String username;

    /**
     * ID
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章描述
     */
    private String description;

    /**
     * 关键字
     */
    private String keywords;

    /**
     * 缩略图
     */
    private String thumbnail;

    /**
     * 文章内容
     */
    private String body;

    /**
     * 创建时间
     */
    private LocalDateTime updatedTime;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 文章分类ID
     */
    private Integer categoryId;

    /**
     * 添加的管理员ID
     */
    private Long userId;

    /**
     * 文章状态；  0 禁用  1 发布
     */
    private Integer status;

    /**
     * 是否置顶
     */
    private Integer isTop;

    /**
     * star数量
     */
    private Integer star;

    /**
     * like数量
     */
    private Integer likes;

    private Integer disLikes;

    /**
     * 评论数量
     */
    private Integer commentsCount;

    @Data
    public static class NavItem {
        private String name;
        private Long id;

        /**
         * 导航类型
         *
         * 1 首页  2 分类  2 内容
         */
        private Integer type;
    }
}
