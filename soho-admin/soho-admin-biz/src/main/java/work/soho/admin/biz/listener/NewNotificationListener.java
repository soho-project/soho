package work.soho.admin.biz.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import work.soho.admin.api.event.NewNotificationEvent;
import work.soho.admin.biz.config.AdminSysConfig;
import work.soho.common.core.support.SpringContextHolder;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.JacksonUtils;
import work.soho.longlink.api.message.LongLinkMessage;
import work.soho.longlink.api.sender.Sender;

@Component
@RequiredArgsConstructor
public class NewNotificationListener {
    private final AdminSysConfig adminSysConfig;

    /**
     * 处理新的通知事件
     *
     * 异步处理
     *
     * @param event
     */
    @Async
    @EventListener
    public void handleNewNotificationEvent(NewNotificationEvent event) {
        // 处理新的通知事件
        if(adminSysConfig.getAdminNoticeAdapter().equalsIgnoreCase("longLink")) {
            Sender sender = SpringContextHolder.getBean(Sender.class);
            if(sender != null) {
                // 发送长链接刷新通知
                LongLinkMessage longLinkMessage = new LongLinkMessage();
                longLinkMessage.setNamespace("admin");
                longLinkMessage.setTopic("notification");
                longLinkMessage.setType("cmd");
                longLinkMessage.setPayload("refresh");
                longLinkMessage.setTraceId(IDGeneratorUtils.snowflake().toString());
                longLinkMessage.setHeaders(null);
                sender.sendToUid(event.getNotification().getAdminUserId().toString(), JacksonUtils.toJson(longLinkMessage));
            }
        }
    }
}
