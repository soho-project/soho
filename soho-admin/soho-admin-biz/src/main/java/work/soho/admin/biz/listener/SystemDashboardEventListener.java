package work.soho.admin.biz.listener;

import lombok.SneakyThrows;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import work.soho.admin.api.event.DashboardEvent;

import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

@Component
public class SystemDashboardEventListener {
    @SneakyThrows
    @EventListener
    public void systemInfo(DashboardEvent event) {
        Properties props = System.getProperties();
        Map map = System.getenv();
        LinkedList<LinkedHashMap<String, Object>> list = new LinkedList<>();
        setData(list, "JDK版本",  Runtime.version().toString());
        setData(list, "CPU",  String.valueOf(Runtime.getRuntime().availableProcessors()));
        setData(list, "可用内存",  Runtime.getRuntime().freeMemory() / 1024L + "K");
        setData(list,"内存总量", Runtime.getRuntime().totalMemory() / 1024L + "K");
        setData(list,"最大内存", Runtime.getRuntime().maxMemory() / 1024L + "K");
        setData(list,"服务器名", map.get("COMPUTERNAME"));
        setData(list,"服务器域名", map.get("USERDOMAIN"));
        event.getListKVCards().add(list);

        //卡片2
        InetAddress addr;
        addr = InetAddress.getLocalHost();
        String ip = addr.getHostAddress();
        LinkedList<LinkedHashMap<String, Object>> kvCard2 = new LinkedList<>();
        setData(kvCard2, "IP",  ip);
        setData(kvCard2, "用户名", map.get("USERNAME"));
        setData(kvCard2, "系统名称", props.getProperty("os.name"));
        setData(kvCard2, "系统架构", props.getProperty("os.arch"));
        setData(kvCard2, "系统版本", props.getProperty("os.version"));
        setData(kvCard2, "工作目录", props.getProperty("user.dir"));
        setData(kvCard2, "用户目录", props.getProperty("user.home"));
        event.getListKVCards().add(kvCard2);
    }

    /**
     * 设置值
     * @param list
     * @param name
     * @param value
     */
    private void setData(LinkedList<LinkedHashMap<String, Object>> list, String name, Object value) {
        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("name", name);
        data.put("percent", value);
        data.put("status", 1);
        list.add(data);
    }
}
