系统配置使用
==========

为了快速读取配置信息， 减少开发代码量， 系统配置信息可以使用注解注入：

    @Value("#{@sohoConfig.getByKey('key2')}")

应为bean默认为单例， 所以一般不会刷新； 如果需要保持同数据库同步，请再对应的bean上添加注解：

    @Scope("prototype")

手动调用接口

```java
package work.soho.api.admin.service;
public interface AdminConfigApiService {
    // 获取指定key的配置信息，泛型T表示返回结果的类型
    <T> T getByKey(String key, Class<T> clazz);
    // 根据key获取配置信息，如果不存在则返回默认值
    <T> T getByKey(String key, Class<T> clazz, T defaultValue);
}
```
## 方法说明
- `getByKey(String key, Class<T> clazz)`：通过给定的key获取配置信息，并将结果转换为泛型类型的T返回。如果没有找到该key对应的配置信息，则抛出异常。
- `getByKey(String key, Class<T> clazz, T defaultValue)`：通过给定的key获取配置信息，并将结果转换为泛型类型的T返回。如果没有找到该key对应的配置信息，则返回defaultValue。
