package work.soho.common.cloud.gateway.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import work.soho.common.cloud.gateway.service.MetaPathsManager;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class RouteLocatorConfig {
    private final MetaPathsManager metaPathsManager;

    @Bean
    @RefreshScope
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        log.info("start create bean RouteLocator");
        RouteLocatorBuilder.Builder builders = builder.routes();
        metaPathsManager.getServicePaths().forEach((serviceName, paths) -> {
            paths.forEach(path -> {
                builders
                        .route(r -> r.order(metaPathsManager.getServiceOrdered(serviceName)).path(path)
//                                .filters(f ->
//                                        f.prefixPath(path)
//                                                .addResponseHeader("X-TestHeader", "foobar"))
                                .uri("lb://" + serviceName)
                        );
            });
        });

        return builders.build();
    }

    @Bean
    public RouterFunction<ServerResponse> testFunRouterFunction() {
        RouterFunction<ServerResponse> route = RouterFunctions.route(RequestPredicates.path("/testfun"),
                request -> ServerResponse.ok().body(BodyInserters.fromValue("hello")));
        return route;
    }
}
