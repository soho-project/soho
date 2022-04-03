package work.soho.api.admin.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class AdminRoleVo {
    private Long id;

    private String name;

    private String remarks;

    private Integer enable;

    private Date createdTime;

    private List<Long> resourceIds;
}
