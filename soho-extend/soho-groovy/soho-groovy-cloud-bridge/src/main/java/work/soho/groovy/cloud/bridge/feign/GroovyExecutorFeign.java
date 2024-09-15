package work.soho.groovy.cloud.bridge.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@FeignClient(name = "soho-groovy-cloud-biz", contextId = "soho-groovy-cloud-biz-2")
public interface GroovyExecutorFeign {
    @PostMapping("/cloud/groovy/croovyExecutorApi/executor")
    Object executor(String code);

    @PostMapping("/cloud/groovy/croovyExecutorApi/executorParams")
    Object executorParams(String code, Map<String, Object> params);
}
