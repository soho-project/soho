package work.soho.chat.biz.req;

import lombok.Data;

/**
 * 更新用户群昵称
 */
@Data
public class UpdateGroupNotesNameReq {
    private Long groupId;
    private Long uid;
    private String notesName;
}
