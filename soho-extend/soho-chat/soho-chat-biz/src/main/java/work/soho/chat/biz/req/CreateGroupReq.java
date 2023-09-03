package work.soho.chat.biz.req;

import lombok.Data;

import java.util.List;

@Data
public class CreateGroupReq {
    private String title;
    private List<Long> chatUserIds;
}
