package work.soho.common.cloud.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Spencer Gibb
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }



//    @Bean
//    public RouterFunction<ServerResponse> testWhenMetricPathIsNotMeet() {
//        RouterFunction<ServerResponse> route = RouterFunctions.route(
//                RequestPredicates.path("/actuator/metrics/spring.cloud.gateway.requests"),
//                request -> ServerResponse.ok()
//                        .body(BodyInserters.fromValue(HELLO_FROM_FAKE_ACTUATOR_METRICS_GATEWAY_REQUESTS)));
//        return route;
//    }
}
