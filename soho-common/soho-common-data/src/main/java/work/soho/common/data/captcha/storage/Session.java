package work.soho.common.data.captcha.storage;


import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import work.soho.common.core.util.RequestUtil;

@Component
public class Session implements StorageInterface{
    @Override
    public void set(String key, Object value) {
        RequestUtil.getSession().setAttribute(key, value);
    }

    @Override
    public Object get(String key) {
        return RequestUtil.getSession().getAttribute(key);
    }

    @Override
    public void drop(String key) {
        RequestUtil.getSession().removeAttribute(key);
    }
}
