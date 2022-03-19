package work.soho.admin.domain;

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
  private String key;
  private long order;
}
