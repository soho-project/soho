package work.soho.admin.api.service;

import work.soho.admin.api.request.AdminConfigInitRequest;

public interface AdminConfigApiService {
    /**
     * 获取后台配置信息
     *
     * @param key
     * @param clazz
     * @return
     * @param <T>
     */
    <T> T getByKey(String key, Class<T> clazz);

    /**
     * 获取指定key的配置值带默认值
     *
     * @param key
     * @param clazz
     * @param defaultValue
     * @return
     * @param <T>
     */
    <T> T getByKey(String key, Class<T> clazz, T defaultValue);

    /**
     * 初始化后台配置
     *
     * @param adminConfigInitRequest
     * @return
     */
    Boolean initItems(AdminConfigInitRequest adminConfigInitRequest);
}
