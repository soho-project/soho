package work.soho.admin.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminConfig implements Serializable {

	private Long id;

	private String key;

	private String value;

	private Long type;

	private String groupName;

}
