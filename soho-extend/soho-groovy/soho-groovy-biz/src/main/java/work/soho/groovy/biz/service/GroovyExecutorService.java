package work.soho.groovy.biz.service;

public interface GroovyExecutorService {
    /**
     * 从groovy代码加载一个实例
     *
     * @param code
     * @return
     */
    Object loadObjectFromCode(String code);
}
