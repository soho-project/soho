package work.soho.api.admin.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

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

    @Data
    public static class Langues {
        private String name;
    }
}
