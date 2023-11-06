package work.soho.chat.biz.vo;

import lombok.Data;

@Data
public class UserVO {
    private Long id;
    private String avatar;
    private String username;
    private String nickname;
    private Boolean isFriend = false;
}
