package work.soho.common.core.webbinding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

@Configuration
public class CustumDateBindConfiguration {
    @Autowired
    public void configurationWebBindingInitializer(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        requestMappingHandlerAdapter.setWebBindingInitializer(new WebBindDate());
        requestMappingHandlerAdapter.setWebBindingInitializer(new WebBindLongArrayPropertyEditor());
    }
}
