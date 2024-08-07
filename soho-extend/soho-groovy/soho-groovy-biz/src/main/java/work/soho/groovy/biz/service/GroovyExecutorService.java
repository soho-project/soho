package work.soho.groovy.biz.service;

import java.util.Map;

public interface GroovyExecutorService {
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

    /**
     * 从groovy代码加载一个实例
     *
     * @param code
     * @return
     */
    Object loadObjectFromCode(String code);
}
