package work.soho.api.admin.po;

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
