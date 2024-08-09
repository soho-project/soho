package work.soho.api.admin.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouteVo {
    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "路由名称")
    private String name;

    @ApiModelProperty(value = "路由地址")
    private String route;

    @ApiModelProperty(value = "路由图标")
    private String icon;

    @ApiModelProperty(value = "父级ID")
    private Long breadcrumbParentId;

    @ApiModelProperty(value = "菜单父级ID")
    private Long menuParentId;

    @ApiModelProperty(value = "路由中文语言")
    private Langues zh;

    @ApiModelProperty(value = "是否可见")
    private Boolean visible;

    @ApiModelProperty(value = "子路由")
    @JsonIgnore
    private List<RouteVo> children;

    @ApiModelProperty(value = "排序")
    @JsonIgnore
    private Integer sort;

    @Data
    public static class Langues {
        @ApiModelProperty(value = "路由中文语言")
        private String name;
    }
}
