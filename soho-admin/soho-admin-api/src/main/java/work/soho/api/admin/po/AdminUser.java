package work.soho.api.admin.po;

import lombok.Data;
import java.io.Serializable;

@Data
public class AdminUser implements Serializable {
  private long id;
  private String nickname;
  private String realname;
  private String email;
  private long phone;
  private String password;
  private java.sql.Timestamp updatedTime;
  private java.sql.Timestamp createdTime;
}
