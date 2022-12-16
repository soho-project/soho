package work.soho.code.biz.service;

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
     * 执行指定模板
     *
     * @param id
     * @param func
     * @param objects
     * @return
     * @param <T>
     */
    public <T> T runById(Integer id, String func, Object... objects);
    public <T> T runByName(String name, String func, Object... objects);
    public <T> T invokeByIdWithName(Integer id, String func, String name);
}
