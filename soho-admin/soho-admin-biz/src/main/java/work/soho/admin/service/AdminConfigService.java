package work.soho.admin.service;

import work.soho.admin.domain.AdminConfig;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author i
* @description 针对表【admin_config】的数据库操作Service
* @createDate 2022-04-05 23:01:25
*/
public interface AdminConfigService extends IService<AdminConfig> {
    /**
     * 获取字符串值
     *
     * @param key
     * @return
     */
    String getByKey(String key);

    /**
     * 获取对象值
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T getByKey(String key, Class<T> clazz);

    /**
     * 获取对象值， 支持默认值
     *
     * @param key
     * @param clazz
     * @param defaultValue
     * @param <T>
     * @return
     */
    <T> T getByKey(String key, Class<T> clazz, T defaultValue);
}
