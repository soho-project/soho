package work.soho.chat.biz.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 更新用户群昵称
 */
@Data
public class UpdateGroupNotesNameReq {
    @ApiModelProperty(value = "群id")
    private Long groupId;

    @ApiModelProperty(value = "用户id")
    private Long uid;

    @ApiModelProperty(value = "群昵称")
    private String notesName;
}
