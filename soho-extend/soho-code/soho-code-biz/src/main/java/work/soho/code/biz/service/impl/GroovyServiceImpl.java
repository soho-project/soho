package work.soho.code.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.springframework.stereotype.Service;
import work.soho.code.biz.domain.CodeTableTemplate;
import work.soho.code.biz.service.CodeTableService;
import work.soho.code.biz.service.CodeTableTemplateService;
import work.soho.code.biz.service.GroovyService;

import javax.script.Bindings;
import javax.script.ScriptEngineManager;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Log4j2
public class GroovyServiceImpl implements GroovyService {
    /**
     * 代码模板服务
     */
    private final CodeTableTemplateService codeTableTemplateService;

    /**
     * 代码导入服务
     */
    private final CodeTableService codeTableService;

    /**
     * 执行脚本
     *
     * @param scriptText
     * @param func
     * @param objs
     * @return
     * @param <T>
     */
    public <T> T invoke03(String scriptText, String func, Object... objs){
        try {
//            Binding binding = new Binding();
//            binding.setVariable("context", this);
//            binding.setVariable("creator", "fang.liu");
//
//            GroovyShell groovyShell = new GroovyShell(binding);
//            binding.setVariable("shell", groovyShell);
//            Script script = groovyShell.parse(scriptText);
//            Object result = InvokerHelper.invokeMethod(script, func, objs);
//            return (T) result;
            return invoke(scriptText, new HashMap(), func, objs);
        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    }

    /**
     * 带环境变量进行绑定
     *
     * @param scriptText
     * @param binds
     * @param func
     * @param objs
     * @return
     * @param <T>
     */
    public <T> T invoke(String scriptText, Map binds, String func, Object... objs){
        try {
            Binding binding = new Binding(binds);
            binding.setVariable("context", this);
            binding.setVariable("creator", "fang.liu");

            GroovyShell groovyShell = new GroovyShell(binding);
            binding.setVariable("shell", groovyShell);
            Script script = groovyShell.parse(scriptText);
            return (T) InvokerHelper.invokeMethod(script, func, objs);
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 执行指定模板代码
     *
     * @param id
     * @param func
     * @param objects
     * @return
     * @param <T>
     */
    public <T> T runById(Integer id, String func, Object... objects) {
        return runById(id, new HashMap(), func, objects);
    }

    @Override
    public <T> T runById(Integer id, Map binds, String func, Object... objects) {
        CodeTableTemplate codeTableTemplate =  codeTableTemplateService.getById(id);
        return invoke(codeTableTemplate.getCode(), binds, func, objects);    }

    /**
     *  根据名字执行脚本模板
     *
     * @param name
     * @param func
     * @param objects
     * @return
     * @param <T>
     */
    public <T> T runByName(String name, String func, Object... objects) {
        CodeTableTemplate codeTableTemplate =  codeTableTemplateService.getOne(new LambdaQueryWrapper<CodeTableTemplate>().eq(CodeTableTemplate::getName, name));
        return invoke03(codeTableTemplate.getCode(), func, objects);
    }

    public <T> T invokeByIdWithName(Integer id, String func, String name) {
        return runById(id, func, name);
    }
}
