package work.soho.api.admin.po;

import lombok.Data;
import java.io.Serializable;

@Data
public class AdminResource implements Serializable {
  private long id;
  private String name;
  private String path;
  private long type;
  private String remarks;
  private java.sql.Timestamp createdTime;
  private long visible;
}
