package work.soho.user.api.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserOauthTypeVo {
    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Integer id;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    private String title;

    /**
     * LOGO
     */
    @ApiModelProperty(value = "LOGO")
    private String logo;

}
