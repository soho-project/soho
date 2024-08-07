package work.soho.chat.biz.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import work.soho.api.admin.service.AdminConfigApiService;

@Component
@RequiredArgsConstructor
public class ImConfig {
    private final AdminConfigApiService adminConfigApiService;

    // 自动人工客服
    public final static String AUTO_CHAT = "auto_chat";

    /**
     * 检查是否自动人工客服
     *
     * @return
     */
    public boolean isAutoChat() {
        return adminConfigApiService.getByKey(AUTO_CHAT, Boolean.class, false);
    }
}
