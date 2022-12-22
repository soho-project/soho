package work.soho.code.biz.service;

import java.util.Map;

public interface GroovyService {
    /**
     * 执行groovy脚本
     *
     * @param scriptText
     * @param func
     * @param objs
     * @return
     * @param <T>
     */
    public <T> T invoke03(String scriptText, String func, Object... objs);

    /**
     * 执行脚本指定方法
     *
     * @param scriptText
     * @param binds 绑定的值
     * @param func
     * @param objs
     * @return
     * @param <T>
     */
    public <T> T invoke(String scriptText, Map binds, String func, Object... objs);

    /**
     * 执行指定模板
     *
     * @param id
     * @param func
     * @param objects
     * @return
     * @param <T>
     */
    public <T> T runById(Integer id, String func, Object... objects);
    public <T> T runById(Integer id, Map binds, String func, Object... objects);
    public <T> T runByName(String name, String func, Object... objects);
    public <T> T invokeByIdWithName(Integer id, String func, String name);
}
