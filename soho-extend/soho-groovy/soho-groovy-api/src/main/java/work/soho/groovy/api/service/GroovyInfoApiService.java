package work.soho.groovy.api.service;

import java.util.Map;

public interface GroovyInfoApiService {
    Object executor(String name);

    Object executor(String name, Map<String, Object> params);
}
