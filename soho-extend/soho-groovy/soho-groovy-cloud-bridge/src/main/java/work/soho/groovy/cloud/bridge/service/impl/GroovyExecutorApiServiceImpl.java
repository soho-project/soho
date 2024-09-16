package work.soho.groovy.cloud.bridge.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.groovy.api.service.GroovyExecutorApiService;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GroovyExecutorApiServiceImpl implements GroovyExecutorApiService {
    private final GroovyExecutorApiService groovyExecutorApiService;

    @Override
    public Object execute(String code) {
        return groovyExecutorApiService.execute(code);
    }

    @Override
    public Object execute(String code, Map<String, Object> params) {
        return groovyExecutorApiService.execute(code, params);
    }
}
