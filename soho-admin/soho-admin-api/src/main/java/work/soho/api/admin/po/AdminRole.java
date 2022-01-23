package work.soho.api.admin.po;

import lombok.Data;
import java.io.Serializable;

@Data
public class AdminRole implements Serializable {
  private long id;
  private String name;
  private String remarks;
  private java.sql.Timestamp createdTime;
}
