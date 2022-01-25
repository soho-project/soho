package work.soho.api.admin.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminRoleResource implements Serializable {

	private Long id;

	private Long roleId;

	private Long resourceId;

	private java.sql.Timestamp createdTime;

}
