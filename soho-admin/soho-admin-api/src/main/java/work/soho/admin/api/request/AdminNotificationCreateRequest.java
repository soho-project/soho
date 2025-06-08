package work.soho.admin.api.request;

import lombok.Data;

import java.util.ArrayList;

@Data
public class AdminNotificationCreateRequest {
    /**
     * 标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 接收者
     */
    private Long adminUserId;

    /**
     * 批量接收者
     */
    private ArrayList<Long> adminUserIds = new ArrayList<>();
}
