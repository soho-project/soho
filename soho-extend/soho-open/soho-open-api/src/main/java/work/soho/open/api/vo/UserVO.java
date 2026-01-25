package work.soho.open.api.vo;

import lombok.Data;

import java.util.Date;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private Integer age;
    private Date birthday;
}
