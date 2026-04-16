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
    private final String SYS_AI_OPENAPI_RATE_LIMIT_ENABLED_KEY = "ai-openapi-rate-limit-enabled";
    private final String SYS_AI_OPENAPI_RATE_LIMIT_PER_MINUTE_KEY = "ai-openapi-rate-limit-per-minute";
    private final String SYS_AI_OPENAPI_BAN_ENABLED_KEY = "ai-openapi-ban-enabled";
    private final String SYS_AI_OPENAPI_BAN_FAIL_THRESHOLD_KEY = "ai-openapi-ban-fail-threshold";
    private final String SYS_AI_OPENAPI_BAN_FAIL_WINDOW_MINUTES_KEY = "ai-openapi-ban-fail-window-minutes";
    private final String SYS_AI_OPENAPI_BAN_DURATION_MINUTES_KEY = "ai-openapi-ban-duration-minutes";

    private final Boolean SYS_AI_OPENAPI_RATE_LIMIT_ENABLED = true;
    private final Integer SYS_AI_OPENAPI_RATE_LIMIT_PER_MINUTE = 60;
    private final Boolean SYS_AI_OPENAPI_BAN_ENABLED = true;
    private final Integer SYS_AI_OPENAPI_BAN_FAIL_THRESHOLD = 5;
    private final Integer SYS_AI_OPENAPI_BAN_FAIL_WINDOW_MINUTES = 10;
    private final Integer SYS_AI_OPENAPI_BAN_DURATION_MINUTES = 30;

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
                .explain("Codex代理类型: http/socks/ss/vmess")
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
        items.add(AdminConfigInitRequest.Item.builder()
                .groupKey(SYS_AI_CONFIG_GROUP)
                .key(SYS_AI_OPENAPI_RATE_LIMIT_ENABLED_KEY)
                .value(String.valueOf(SYS_AI_OPENAPI_RATE_LIMIT_ENABLED))
                .type(AdminConfigInitRequest.ItemType.TEXT.getType())
                .explain("是否启用 Guest OpenAI API Key 分钟限流")
                .build());
        items.add(AdminConfigInitRequest.Item.builder()
                .groupKey(SYS_AI_CONFIG_GROUP)
                .key(SYS_AI_OPENAPI_RATE_LIMIT_PER_MINUTE_KEY)
                .value(String.valueOf(SYS_AI_OPENAPI_RATE_LIMIT_PER_MINUTE))
                .type(AdminConfigInitRequest.ItemType.TEXT.getType())
                .explain("Guest OpenAI API Key 每分钟最大请求数")
                .build());
        items.add(AdminConfigInitRequest.Item.builder()
                .groupKey(SYS_AI_CONFIG_GROUP)
                .key(SYS_AI_OPENAPI_BAN_ENABLED_KEY)
                .value(String.valueOf(SYS_AI_OPENAPI_BAN_ENABLED))
                .type(AdminConfigInitRequest.ItemType.TEXT.getType())
                .explain("是否启用 Guest OpenAI 异常临时封禁")
                .build());
        items.add(AdminConfigInitRequest.Item.builder()
                .groupKey(SYS_AI_CONFIG_GROUP)
                .key(SYS_AI_OPENAPI_BAN_FAIL_THRESHOLD_KEY)
                .value(String.valueOf(SYS_AI_OPENAPI_BAN_FAIL_THRESHOLD))
                .type(AdminConfigInitRequest.ItemType.TEXT.getType())
                .explain("触发临时封禁的异常失败次数阈值")
                .build());
        items.add(AdminConfigInitRequest.Item.builder()
                .groupKey(SYS_AI_CONFIG_GROUP)
                .key(SYS_AI_OPENAPI_BAN_FAIL_WINDOW_MINUTES_KEY)
                .value(String.valueOf(SYS_AI_OPENAPI_BAN_FAIL_WINDOW_MINUTES))
                .type(AdminConfigInitRequest.ItemType.TEXT.getType())
                .explain("异常失败统计窗口(分钟)")
                .build());
        items.add(AdminConfigInitRequest.Item.builder()
                .groupKey(SYS_AI_CONFIG_GROUP)
                .key(SYS_AI_OPENAPI_BAN_DURATION_MINUTES_KEY)
                .value(String.valueOf(SYS_AI_OPENAPI_BAN_DURATION_MINUTES))
                .type(AdminConfigInitRequest.ItemType.TEXT.getType())
                .explain("临时封禁时长(分钟)")
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

    public boolean isOpenApiRateLimitEnabled() {
        return adminConfigApiService.getByKey(SYS_AI_OPENAPI_RATE_LIMIT_ENABLED_KEY, Boolean.class, SYS_AI_OPENAPI_RATE_LIMIT_ENABLED);
    }

    public int getOpenApiRateLimitPerMinute() {
        return adminConfigApiService.getByKey(SYS_AI_OPENAPI_RATE_LIMIT_PER_MINUTE_KEY, Integer.class, SYS_AI_OPENAPI_RATE_LIMIT_PER_MINUTE);
    }

    public boolean isOpenApiBanEnabled() {
        return adminConfigApiService.getByKey(SYS_AI_OPENAPI_BAN_ENABLED_KEY, Boolean.class, SYS_AI_OPENAPI_BAN_ENABLED);
    }

    public int getOpenApiBanFailThreshold() {
        return adminConfigApiService.getByKey(SYS_AI_OPENAPI_BAN_FAIL_THRESHOLD_KEY, Integer.class, SYS_AI_OPENAPI_BAN_FAIL_THRESHOLD);
    }

    public int getOpenApiBanFailWindowMinutes() {
        return adminConfigApiService.getByKey(SYS_AI_OPENAPI_BAN_FAIL_WINDOW_MINUTES_KEY, Integer.class, SYS_AI_OPENAPI_BAN_FAIL_WINDOW_MINUTES);
    }

    public int getOpenApiBanDurationMinutes() {
        return adminConfigApiService.getByKey(SYS_AI_OPENAPI_BAN_DURATION_MINUTES_KEY, Integer.class, SYS_AI_OPENAPI_BAN_DURATION_MINUTES);
    }
}
