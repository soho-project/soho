package work.soho.api.admin.po;

import lombok.Data;
import java.io.Serializable;

@Data
public class AdminConfig implements Serializable {
  private long id;
  private String key;
  private String value;
  private long type;
  private String groupName;
}
