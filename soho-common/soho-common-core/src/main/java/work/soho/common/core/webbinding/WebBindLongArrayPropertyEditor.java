package work.soho.common.core.webbinding;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;

public class WebBindLongArrayPropertyEditor implements WebBindingInitializer {
    @Override
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Long[].class, new LongArrayPropertyEditor());
    }
}
