package work.soho.api.admin.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
public class CurrentAdminUserVo {
    private Long id;
    private String avatar;
    private String username;
    private Permissions permissions;

    @Data
    @Accessors(chain = true)
    public static class Permissions {
        private String role;
    }
}
