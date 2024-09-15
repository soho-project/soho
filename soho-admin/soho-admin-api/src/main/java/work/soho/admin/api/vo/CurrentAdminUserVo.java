package work.soho.admin.api.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;

@Data
public class CurrentAdminUserVo {
    @ApiModelProperty(value = "用户ID")
    private Long id;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "用户权限")
    private Permissions permissions;

    @Data
    @Accessors(chain = true)
    public static class Permissions {
        @ApiModelProperty(value = "用户角色")
        private String role; //用户角色

        @ApiModelProperty(value = "用户权限")
        ArrayList<Long> visit; //可见资源ID集合
    }
}
