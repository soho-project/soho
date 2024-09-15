package work.soho.groovy.cloud.bridge.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.groovy.cloud.bridge.feign.GroovyInfoFeign;
import work.soho.groovy.service.GroovyInfoApiService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GroovyApiServiceImpl implements GroovyInfoApiService {
    private final GroovyInfoFeign groovyInfoFeign;

    @Override
    public Object executor(String name) {
        return groovyInfoFeign.executor(name);
    }

    @Override
    public Object executor(String name, Map<String, Object> params) {
        return groovyInfoFeign.executorParams(name, params);
    }
}
