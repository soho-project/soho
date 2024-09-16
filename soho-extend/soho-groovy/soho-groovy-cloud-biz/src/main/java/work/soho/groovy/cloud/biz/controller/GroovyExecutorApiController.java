package work.soho.groovy.cloud.biz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.groovy.api.service.GroovyExecutorApiService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cloud/groovy/croovyExecutorApi")
public class GroovyExecutorApiController {
    private final GroovyExecutorApiService groovyExecutorApiService;

    /**
     * 执行指定脚本
     *
     * @param code
     * @return
     */
    @RequestMapping("executor")
    public Object executor(String code) {
        return groovyExecutorApiService.execute(code);
    }

    /**
     * 执行指定脚本带参数
     *
     * @param code
     * @param params
     * @return
     */
    @RequestMapping("executorParams")
    public Object executorParams(String code, Map<String, Object> params) {
        return groovyExecutorApiService.execute(code, params);
    }
}
