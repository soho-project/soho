package work.soho.api.admin.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminResource implements Serializable {

	private Long id;

	private String name;

	private String path;

	private Long type;

	private String remarks;

	private java.sql.Timestamp createdTime;

	private Long visible;

}
