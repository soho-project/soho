package work.soho.groovy.cloud.biz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.groovy.service.GroovyInfoApiService;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cloud/groovy/croovyInfoApi")
public class CloudGroovyApiController {
    private final GroovyInfoApiService groovyInfoApiService;

    /**
     * 执行指定脚本
     *
     * @param name
     * @return
     */
    @GetMapping("executor")
    public Object executor(String name) {
        return groovyInfoApiService.executor(name);
    }

    /**
     * 执行指定脚本带参数
     *
     * @param name
     * @param params
     * @return
     */
    @GetMapping("executorParams")
    public Object executorParams(String name, HashMap<String, Object> params) {
        return groovyInfoApiService.executor(name, params);
    }
}
