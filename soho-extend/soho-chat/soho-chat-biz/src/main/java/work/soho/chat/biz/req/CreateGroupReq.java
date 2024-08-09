package work.soho.chat.biz.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CreateGroupReq {
    @ApiModelProperty(value = "群组名称")
    private String title;

    @ApiModelProperty(value = "群组头像")
    private List<Long> chatUserIds;
}
