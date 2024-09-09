package work.soho.common.cloud.gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetaPathsManager {
    private final DiscoveryClient discoveryClient;

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
            return 0; // 或者 throw new RuntimeException("No instances found for service: " + service);
        }

        for (ServiceInstance instance : instances) {
            Map<String, String> metadata = instance.getMetadata();
            if (metadata != null && metadata.containsKey("routeOrder")) {
                String val = metadata.get("routeOrder");
                try {
                    // 尝试将字符串转换为整数
                    return Integer.parseInt(val);
                } catch (NumberFormatException e) {
                    // 如果转换失败，记录错误并继续尝试下一个实例（或根据需要调整逻辑）
                    // 这里只是打印日志，您可以根据需要进行调整
//                    log.error("Invalid routeOrder value for service {}: {}", service, val, e);
                    // 如果不希望在第一个无效值后就停止，可以继续循环
                    // 但由于我们使用了findFirst，这里其实已经跳出了循环
                }
            }
        }

        // 如果没有找到有效的routeOrder，返回null或默认值
        return 0; // 或者返回0，但注意这可能会隐藏配置错误
    }
}
