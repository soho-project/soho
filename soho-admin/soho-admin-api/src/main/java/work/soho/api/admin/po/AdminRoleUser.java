package work.soho.api.admin.po;

import lombok.Data;
import java.io.Serializable;

@Data
public class AdminRoleUser implements Serializable {
  private long id;
  private long roleId;
  private long userId;
  private long status;
  private java.sql.Timestamp createdTime;
}
