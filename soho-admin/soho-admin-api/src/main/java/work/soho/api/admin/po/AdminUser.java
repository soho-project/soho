package work.soho.api.admin.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminUser implements Serializable {

	private Long id;

	private String nickname;

	private String realName;

	private String email;

	private Long phone;

	private String password;

	private java.sql.Timestamp updatedTime;

	private java.sql.Timestamp createdTime;

}
