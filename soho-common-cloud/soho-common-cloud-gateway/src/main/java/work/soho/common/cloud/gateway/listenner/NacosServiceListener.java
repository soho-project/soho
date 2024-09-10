package work.soho.common.cloud.gateway.listenner;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Properties;

@Log4j2
@Component
@RequiredArgsConstructor
public class NacosServiceListener implements CommandLineRunner {

    private final NacosDiscoveryProperties nacosDiscoveryProperties;
    private NamingService namingService;

    private final ApplicationContext applicationContext;

    private final RefreshScope refreshScope;

    @PostConstruct
    public void init() throws Exception {
        Properties properties = new Properties();
        properties.put("serverAddr", nacosDiscoveryProperties.getServerAddr());
        this.namingService = NacosFactory.createNamingService(properties);
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. 监听所有已存在服务的实例变化
        listenToAllServiceInstances();

        // 2. 定期轮询获取服务列表，监听新加入的服务
        new Thread(() -> {
            while (true) {
                try {
                    // 定期获取所有服务列表
                    List<String> services = namingService.getServicesOfServer(1, Integer.MAX_VALUE).getData();
                    for (String serviceName : services) {
                        // 为每个服务注册实例变化监听器
                        namingService.subscribe(serviceName, event -> {
                            if (event instanceof NamingEvent) {
                                NamingEvent namingEvent = (NamingEvent) event;
                                log.info("服务 " + serviceName + " 发生实例变化: " + namingEvent.getInstances());
                                refreshScope.refresh(RouteLocator.class.getName());
                            }
                        });
                    }
                    // 每隔10秒刷新服务列表
                    Thread.sleep(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void listenToAllServiceInstances() {
        try {
            List<String> services = namingService.getServicesOfServer(1, Integer.MAX_VALUE).getData();
            for (String serviceName : services) {
                namingService.subscribe(serviceName, event -> {
                    if (event instanceof NamingEvent) {
                        NamingEvent namingEvent = (NamingEvent) event;
                        System.out.println("服务 " + serviceName + " 的实例发生变化: " + namingEvent.getInstances());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
