package work.soho.chat.biz.vo;

import lombok.Data;

@Data
public class BaseUserVO {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
}
