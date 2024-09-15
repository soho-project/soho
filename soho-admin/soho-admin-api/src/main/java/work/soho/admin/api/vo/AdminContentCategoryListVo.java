package work.soho.admin.api.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class AdminContentCategoryListVo {
    /**
     * 分类导航
     */
    private List<AdminContentVo.NavItem> navs = new ArrayList<>();

    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 分类关键字
     */
    private String keyword;

    /**
     * 分类内容
     */
    private String content;

    /**
     * 排序
     */
    private Integer order;

    /**
     * 分类是否显示 0 不显示  1 显示
     */
    private Integer isDisplay;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

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
