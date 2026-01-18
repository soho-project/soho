package work.soho.admin.api.event;

import lombok.Data;
import work.soho.admin.api.vo.AdminNotificationVo;

/**
 * 新消息事件
 */
@Data
public class NewNotificationEvent {
    private AdminNotificationVo notification;
}
