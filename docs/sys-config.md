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
- `Boolean initItems(AdminConfigInitRequest adminConfigInitRequest);`: 模块初始化系统配置接口

## 模块初始化系统配置

请参考：
        
        //系统会检查 group  item是否已经初始化， 如果已经存在则不做任何处理
        @Service
            @RequiredArgsConstructor
            public class TemporalDbConfig implements InitializingBean {
            private final String DEFAULT_DB_NAME_KEY = "default_db_name_key";
            private final String DEFAULT_DB_NAME = "root.soho";
            private final String DEFAULT_GROUP_NAME = "temporal_db";
        
            private final AdminConfigApiService adminConfigApiService;
            
            @Override
            public void afterPropertiesSet() throws Exception {
                ArrayList<AdminConfigInitRequest.Group> groups = new ArrayList<>();
                groups.add(AdminConfigInitRequest.Group.builder().key(DEFAULT_GROUP_NAME).name("时序数据库").build());
                
                AdminConfigInitRequest.Item item = AdminConfigInitRequest.Item.builder()
                        .key(DEFAULT_DB_NAME_KEY)
                        .groupKey(DEFAULT_GROUP_NAME)
                        .value(DEFAULT_DB_NAME)
                        .explain("默认时序数据库名")
                        .type(AdminConfigInitRequest.ItemType.TEXT.getType())
                        .build();
                ArrayList<AdminConfigInitRequest.Item> items = new ArrayList<>();
                items.add(item);
                adminConfigApiService.initItems(AdminConfigInitRequest.builder().items(items).groupList(groups).build());
            }
        }