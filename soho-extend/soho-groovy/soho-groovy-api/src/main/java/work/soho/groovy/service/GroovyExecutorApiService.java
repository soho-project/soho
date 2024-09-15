package work.soho.groovy.service;

import java.util.Map;

public interface GroovyExecutorApiService {
    /**
     * 执行一个groovy程序
     *
     * @param code 程序代码
     * @return 程序的输出
     */
    Object execute(String code);

    /**
     * 执行一个groovy程序
     *
     * @param code 程序代码
     * @param params 参数
     * @return 程序的输出
     */
    Object execute(String code, Map<String, Object> params);

}
