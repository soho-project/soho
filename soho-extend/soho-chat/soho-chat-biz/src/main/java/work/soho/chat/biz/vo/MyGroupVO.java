package work.soho.chat.biz.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class MyGroupVO {
    /**
     * 群ID
     */
    private Long id;

    /**
     * 群组标题
     */
    private String title;

    /**
     * 群组头像
     */
    private String avatar;

    /**
     * 群备注
     */
    private String notesName;

    /**
     * 当前请求用户是否为管理员
     */
    private Integer isAdmin;

    /**
     * 主管理员
     */
    private Long masterChatUid;
}
