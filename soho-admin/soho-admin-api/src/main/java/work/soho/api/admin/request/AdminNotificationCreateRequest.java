package work.soho.api.admin.request;

import lombok.Data;

@Data
public class AdminNotificationCreateRequest {
    private String title;
    private String content;
    private Long[] adminUserIds;
}
