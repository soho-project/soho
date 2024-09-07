package work.soho.common.cloud.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * @author Spencer Gibb
 */
@SpringBootConfiguration
@EnableAutoConfiguration
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        //@formatter:off
        // String uri = "http://httpbin.org:80";
        // String uri = "http://localhost:9080";
        return builder.routes()
                .route(r -> r.host("localhost").and().path("/anything/png")
                        .filters(f ->
                                f.prefixPath("/httpbin")
                                        .addResponseHeader("X-TestHeader", "foobar"))
                        .uri("http://localhost:6677")
                )
                .build();
        //@formatter:on
    }

    @Bean
    public RouterFunction<ServerResponse> testFunRouterFunction() {
        RouterFunction<ServerResponse> route = RouterFunctions.route(RequestPredicates.path("/testfun"),
                request -> ServerResponse.ok().body(BodyInserters.fromValue("hello")));
        return route;
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
