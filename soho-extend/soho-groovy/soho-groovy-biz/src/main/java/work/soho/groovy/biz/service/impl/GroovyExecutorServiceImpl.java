package work.soho.groovy.biz.service.impl;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;
import org.springframework.stereotype.Service;
import work.soho.groovy.biz.service.GroovyExecutorService;
import work.soho.groovy.api.service.GroovyExecutorApiService;

import java.util.Map;

@Service
public class GroovyExecutorServiceImpl implements GroovyExecutorService, GroovyExecutorApiService {
    @Override
    public Object execute(String code) {
        return execute(code, null);
    }

    /**
     * 执行指定代码
     *
     * @param code
     * @param params
     * @return
     */
    @Override
    public Object execute(String code, Map<String, Object> params) {
        GroovyShell groovyShell = new GroovyShell();
        if(params!= null &&!params.isEmpty()) {
            params.forEach((key, value) -> {
               groovyShell.setVariable(key, value);
            });
        }
        return groovyShell.parse(code).run();
    }

    /**
     * 从代码加载对象
     *
     * @param code
     * @return
     */
    @Override
    public Object loadObjectFromCode(String code) {
        try {
            // Create a GroovyClassLoader and load the class
            GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
            Class<?> groovyClass = groovyClassLoader.parseClass(code);

            // Create an instance of the Groovy class
            GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();

            // Call the 'sayHello' method on the Groovy class
//            groovyObject.invokeMethod("sayHello", null);
            return groovyObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从代码装配对象
     *
     * @param code
     * @param params
     * @return
     */
    public Object loadObjectFromCode(String code, Map<String, String> params) {
        try {
            // Create a GroovyClassLoader and load the class
            GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
            Class<?> groovyClass = groovyClassLoader.parseClass(code);

            // Create an instance of the Groovy class
            GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();

            if(params != null &&!params.isEmpty()) {
                //bind the params
                params.forEach( (key, value) -> {
                    System.out.println("key: " + key + " value: " + value);
                    groovyObject.setProperty(key, value);
                });
            }
            return groovyObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
