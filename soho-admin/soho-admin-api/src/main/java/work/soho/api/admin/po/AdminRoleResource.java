package work.soho.api.admin.po;

import lombok.Data;
import java.io.Serializable;

@Data
public class AdminRoleResource implements Serializable {
  private long id;
  private long roleId;
  private long resourceId;
  private java.sql.Timestamp createdTime;
}
