package work.soho.api.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@ToString
@Accessors(chain = true)
public class AdminUserVo {
    private Long id;
    private String username;
    private String nickName;
    private String avatar;
    private String realName;
    private String phone;
    private Integer sex;
    private Integer age;
    private String email;
    private String password;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;
    private List<Long> roleIds;
}
