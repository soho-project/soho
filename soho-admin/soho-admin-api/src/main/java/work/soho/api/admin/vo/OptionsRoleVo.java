package work.soho.api.admin.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OptionsRoleVo {
    private Long id;
    private String name;
}
