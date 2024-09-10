package work.soho.common.cloud.gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class MetaPathsManager {
    private final DiscoveryClient discoveryClient;

    /**
     * 获取服务对应的路由路径
     *
     * @return
     */
    public Map<String, Set<String>> getServicePaths() {
        List<String> services = discoveryClient.getServices();
        HashMap<String, Set<String>> servicePaths = new HashMap<>();
        for (String service : services) {
            Set<String> pathsSet = discoveryClient.getInstances(service).stream()
                    .map(instance -> instance.getMetadata())
                    .flatMap(metadata -> metadata.entrySet().stream()
                            .filter(entry -> entry.getKey().startsWith("paths."))
                            .map(Map.Entry::getValue))
                    .collect(Collectors.toSet());
            if(servicePaths.containsKey(service)) {
                servicePaths.get(service).addAll(pathsSet);
            } else {
                servicePaths.put(service, pathsSet);
            }
        }
        return servicePaths;
    }

    /**
     * 获取服务优先级
     *
     * @param service
     * @return
     */
    public Integer getServiceOrdered(String service) {
        List<ServiceInstance> instances = discoveryClient.getInstances(service);
        if (instances.isEmpty()) {
            // 如果没有找到实例，可以选择返回null或抛出异常
            return 0;
        }

        for (ServiceInstance instance : instances) {
            Map<String, String> metadata = instance.getMetadata();
            if (metadata != null && metadata.containsKey("routeOrder")) {
                String val = metadata.get("routeOrder");
                try {
                    // 尝试将字符串转换为整数
                    return Integer.parseInt(val);
                } catch (NumberFormatException e) {
                    log.error("Invalid routeOrder value: " + val);
                }
            }
        }

        // 如果没有找到有效的routeOrder，返回null或默认值
        return 0; // 或者返回0，但注意这可能会隐藏配置错误
    }
}
