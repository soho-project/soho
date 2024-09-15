package work.soho.groovy.cloud.bridge.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@FeignClient(name = "soho-groovy-cloud-biz", contextId = "soho-groovy-cloud-biz-1")
public interface GroovyInfoFeign {
    @GetMapping("/cloud/groovy/croovyInfoApi/executor")
    Object executor(String name);

    @PostMapping("/cloud/groovy/croovyInfoApi/executorParams")
    Object executorParams(String name, Map<String, Object> params);
}
