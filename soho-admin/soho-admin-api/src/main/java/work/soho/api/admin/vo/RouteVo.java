package work.soho.api.admin.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouteVo {
    private Long id;
    private String name;
    private String route;
    private String icon;
    private Long breadcrumbParentId;
    private Long menuParentId;
    private Langues zh;
    private Boolean visible;

    @JsonIgnore
    private List<RouteVo> children;
    @JsonIgnore
    private Integer sort;

    @Data
    public static class Langues {
        private String name;
    }
}
