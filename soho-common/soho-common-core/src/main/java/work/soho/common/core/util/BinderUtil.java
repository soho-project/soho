package work.soho.common.core.util;

import lombok.experimental.UtilityClass;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import work.soho.common.core.support.SpringContextHolder;

@UtilityClass
public class BinderUtil {
    /**
     * 获取binder
     *
     * @return
     */
    public Binder getBinder() {
        return Binder.get(SpringContextHolder.getBean(Environment.class));
    }

    /**
     * 绑定配置到指定类
     *
     * @param name
     * @param target
     * @param <T>
     * @return
     */
    public <T> T bind(String name, Class<T> target) {
        return getBinder().bind(name, target).get();
    }
}
