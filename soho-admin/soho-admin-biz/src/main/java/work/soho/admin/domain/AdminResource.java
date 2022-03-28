package work.soho.admin.domain;

import lombok.Data;
import java.io.Serializable;

@Data
public class AdminResource implements Serializable {
  private long id;
  private String name;
  private String route;
  private Long breadcrumbParentId;
  private String icon;
  private long type;
  private String remarks;
  private java.sql.Timestamp createdTime;
  private long visible;
  private long sort;
  private String zhName;
}
