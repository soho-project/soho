package work.soho.ai.biz.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import work.soho.admin.api.request.AdminConfigInitRequest;
import work.soho.admin.api.service.AdminConfigApiService;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AiSysConfig implements InitializingBean {
    private final String SYS_AI_CONFIG_GROUP = "ai";

    private final String SYS_AI_CODEX_PROXY_TYPE_KEY = "ai-codex-proxy-type";
    private final String SYS_AI_CODEX_PROXY_HOST_KEY = "ai-codex-proxy-host";
    private final String SYS_AI_CODEX_PROXY_PORT_KEY = "ai-codex-proxy-port";

    private final String SYS_AI_CODEX_PROXY_TYPE = "";
    private final String SYS_AI_CODEX_PROXY_HOST = "";
    private final Integer SYS_AI_CODEX_PROXY_PORT = 0;

    private final AdminConfigApiService adminConfigApiService;

    @Override
    public void afterPropertiesSet() {
        ArrayList<AdminConfigInitRequest.Group> groups = new ArrayList<>();
        groups.add(AdminConfigInitRequest.Group.builder().key(SYS_AI_CONFIG_GROUP).name("AI配置").build());

        ArrayList<AdminConfigInitRequest.Item> items = new ArrayList<>();
        items.add(AdminConfigInitRequest.Item.builder()
                .groupKey(SYS_AI_CONFIG_GROUP)
                .key(SYS_AI_CODEX_PROXY_TYPE_KEY)
                .value(SYS_AI_CODEX_PROXY_TYPE)
                .type(AdminConfigInitRequest.ItemType.TEXT.getType())
                .explain("Codex代理类型: http/socks")
                .build());
        items.add(AdminConfigInitRequest.Item.builder()
                .groupKey(SYS_AI_CONFIG_GROUP)
                .key(SYS_AI_CODEX_PROXY_HOST_KEY)
                .value(SYS_AI_CODEX_PROXY_HOST)
                .type(AdminConfigInitRequest.ItemType.TEXT.getType())
                .explain("Codex代理主机地址")
                .build());
        items.add(AdminConfigInitRequest.Item.builder()
                .groupKey(SYS_AI_CONFIG_GROUP)
                .key(SYS_AI_CODEX_PROXY_PORT_KEY)
                .value(String.valueOf(SYS_AI_CODEX_PROXY_PORT))
                .type(AdminConfigInitRequest.ItemType.TEXT.getType())
                .explain("Codex代理端口")
                .build());

        adminConfigApiService.initItems(AdminConfigInitRequest.builder().items(items).groupList(groups).build());
    }

    public String getCodexProxyType() {
        return adminConfigApiService.getByKey(SYS_AI_CODEX_PROXY_TYPE_KEY, String.class, SYS_AI_CODEX_PROXY_TYPE);
    }

    public String getCodexProxyHost() {
        return adminConfigApiService.getByKey(SYS_AI_CODEX_PROXY_HOST_KEY, String.class, SYS_AI_CODEX_PROXY_HOST);
    }

    public Integer getCodexProxyPort() {
        return adminConfigApiService.getByKey(SYS_AI_CODEX_PROXY_PORT_KEY, Integer.class, SYS_AI_CODEX_PROXY_PORT);
    }
}
