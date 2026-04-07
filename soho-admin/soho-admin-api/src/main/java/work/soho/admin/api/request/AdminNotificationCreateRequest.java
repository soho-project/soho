package work.soho.admin.api.request;

import lombok.Data;

import java.util.ArrayList;

@Data
public class AdminNotificationCreateRequest {
    /**
     * 接收范围：custom/all。
     */
    private String receiverScope;

    /**
     * 接收者类型：admin/user
     */
    private String receiverType;

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
     * 前端用户接收者
     */
    private Long userId;

    /**
     * 批量接收者
     */
    private ArrayList<Long> adminUserIds = new ArrayList<>();

    /**
     * 批量前端用户接收者
     */
    private ArrayList<Long> userIds = new ArrayList<>();
}
