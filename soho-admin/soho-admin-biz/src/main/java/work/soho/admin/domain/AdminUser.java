package work.soho.admin.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AdminUser implements Serializable {

	@TableId(type = IdType.AUTO)
	private Long id;

	private String username;

	private String avatar;

	private String nickName;

	private String realName;

	private String email;

	private Long phone;

	private String password;

	private Date updatedTime;

	private Date createdTime;

	private Integer age;

	private Integer sex;

	private Integer isDeleted;

}
