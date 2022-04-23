package work.soho.api.admin.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;

@Data
public class CurrentAdminUserVo {
    private Long id;
    private String avatar;
    private String username;
    private Permissions permissions;

    @Data
    @Accessors(chain = true)
    public static class Permissions {
        private String role; //用户角色
        ArrayList<Long> visit; //可见资源ID集合
    }
}
