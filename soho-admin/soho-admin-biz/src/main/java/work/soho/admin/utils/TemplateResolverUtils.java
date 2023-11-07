package work.soho.admin.utils;

import lombok.experimental.UtilityClass;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.Map;

@UtilityClass
public class TemplateResolverUtils {
    /**
     * 获取模板解析引擎
     *
     * @return
     */
    private ITemplateResolver htmlTemplateResolver() {
        StringTemplateResolver resolver = new StringTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        return resolver;
    }

    /**
     * 获取模板解析引擎
     *
     * @return
     */
    private ITemplateResolver stringTemplateResolver() {
        StringTemplateResolver resolver = new StringTemplateResolver();
        resolver.setTemplateMode(TemplateMode.TEXT);
        return resolver;
    }

    /**
     * 解析模板成字符串
     *
     * @param textTemplate
     * @param model
     * @return
     */
    public String resolverHtml(String textTemplate, Map<String, Object> model) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(htmlTemplateResolver());

        Context context = new Context();
        context.setVariables(model);
        String text = engine.process(textTemplate, context);
        return text;
    }

    /**
     * 解析模板成字符串
     *
     * @param textTemplate
     * @param model
     * @return
     */
    public String resolverString(String textTemplate, Map<String, Object> model) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(stringTemplateResolver());

        Context context = new Context();
        context.setVariables(model);
        String text = engine.process(textTemplate, context);
        return text;
    }
}
