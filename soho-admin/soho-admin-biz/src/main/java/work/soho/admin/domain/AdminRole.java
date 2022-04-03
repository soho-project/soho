package work.soho.admin.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("admin_role")
public class AdminRole implements Serializable {
	@TableId(type = IdType.AUTO)
	private Long id;

	private String name;

	private String remarks;

	private Integer enable;

	private Date createdTime;

}
