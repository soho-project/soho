package work.soho.admin.biz.listener;

import lombok.SneakyThrows;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import work.soho.admin.api.dashboard.DashboardKvCardProvider;
import work.soho.admin.api.vo.DashboardBuildContext;

import java.net.InetAddress;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 系统信息键值卡片提供者。
 */
@Component
@Order(100)
public class SystemDashboardKvCardProvider implements DashboardKvCardProvider {
    @Override
    @SneakyThrows
    public List<List> provide(DashboardBuildContext context) {
        Properties props = System.getProperties();
        Map env = System.getenv();
        LinkedList<LinkedHashMap<String, Object>> firstCard = new LinkedList<>();
        setData(firstCard, "JDK版本", Runtime.version().toString());
        setData(firstCard, "CPU", String.valueOf(Runtime.getRuntime().availableProcessors()));
        setData(firstCard, "可用内存", Runtime.getRuntime().freeMemory() / 1024L + "K");
        setData(firstCard, "内存总量", Runtime.getRuntime().totalMemory() / 1024L + "K");
        setData(firstCard, "最大内存", Runtime.getRuntime().maxMemory() / 1024L + "K");
        setData(firstCard, "服务器名", env.get("COMPUTERNAME"));
        setData(firstCard, "服务器域名", env.get("USERDOMAIN"));

        InetAddress addr = InetAddress.getLocalHost();
        LinkedList<LinkedHashMap<String, Object>> secondCard = new LinkedList<>();
        setData(secondCard, "IP", addr.getHostAddress());
        setData(secondCard, "用户名", env.get("USERNAME"));
        setData(secondCard, "系统名称", props.getProperty("os.name"));
        setData(secondCard, "系统架构", props.getProperty("os.arch"));
        setData(secondCard, "系统版本", props.getProperty("os.version"));
        setData(secondCard, "工作目录", props.getProperty("user.dir"));
        setData(secondCard, "用户目录", props.getProperty("user.home"));
        return List.of(firstCard, secondCard);
    }

    /**
     * 添加键值项。
     *
     * @param list 数据列表
     * @param name 名称
     * @param value 值
     */
    private void setData(LinkedList<LinkedHashMap<String, Object>> list, String name, Object value) {
        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("name", name);
        data.put("percent", value);
        data.put("status", 1);
        list.add(data);
    }
}
