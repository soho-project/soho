package work.soho.admin.biz.service;

import work.soho.admin.biz.domain.AdminConfig;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.Map;

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

    /**
     * 批量获取指定 key 的配置值。
     *
     * @param keys 配置 key 集合
     * @return key 与 value 映射
     */
    Map<String, String> getByKeys(Collection<String> keys);
}
