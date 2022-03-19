package work.soho.admin.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminRole implements Serializable {

	private Long id;

	private String name;

	private String remarks;

	private java.sql.Timestamp createdTime;

}
