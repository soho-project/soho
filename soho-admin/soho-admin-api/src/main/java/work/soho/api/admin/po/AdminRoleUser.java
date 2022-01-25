package work.soho.api.admin.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminRoleUser implements Serializable {

	private Long id;

	private Long roleId;

	private Long userId;

	private Integer status;

	private java.sql.Timestamp createdTime;

}
