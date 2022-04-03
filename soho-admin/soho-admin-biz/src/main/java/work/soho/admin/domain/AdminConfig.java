package work.soho.admin.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class AdminConfig implements Serializable {
	@TableId(type = IdType.AUTO)
	private Long id;

	private String key;

	private String value;

	private Long type;

	private String groupName;

}
