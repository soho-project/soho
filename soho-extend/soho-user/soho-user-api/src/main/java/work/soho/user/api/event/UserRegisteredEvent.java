package work.soho.user.api.event;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserRegisteredEvent implements Serializable {
    private Long userId;
    private String code;
    private String username;
    private String phone;
    private String email;
    private String nickname;
    private LocalDateTime registeredTime;
}
