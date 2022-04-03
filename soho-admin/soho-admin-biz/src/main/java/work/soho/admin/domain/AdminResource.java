package work.soho.admin.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

@Data
public class AdminResource implements Serializable {
  @TableId(type = IdType.AUTO)
  private Long id;
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
