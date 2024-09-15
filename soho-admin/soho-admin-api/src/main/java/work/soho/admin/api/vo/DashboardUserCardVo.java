package work.soho.admin.api.vo;

import lombok.Data;

import java.util.LinkedList;

@Data
public class DashboardUserCardVo {
    private Long userId;
    private String username;
    private String avatar;

    private LinkedList<Info>  listInfo = new LinkedList<>();

    @Data
    public static class Info {
        private String title;
        private Object value;
    }
}
