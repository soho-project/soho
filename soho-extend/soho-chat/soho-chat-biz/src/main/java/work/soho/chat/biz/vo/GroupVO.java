package work.soho.chat.biz.vo;

import lombok.Data;

@Data
public class GroupVO {
    private Long id;
    private String title;
    private String avatar;

    /**
     * 用户是否在群里
     */
    private Boolean inGroup;
}
