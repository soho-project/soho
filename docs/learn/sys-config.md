# 系统配置服务

## 简介

系统配置服务提供了一个集中管理动态配置项的功能。它允许你将配置信息存储在数据库中，并在应用运行时进行读取，从而避免了将配置硬编码在代码或配置文件中。这对于需要随时调整的参数（如第三方服务的开关、活动信息、业务阈值等）非常有用。

核心服务由 `soho-admin-biz` 模块中的 `AdminConfigServiceImpl` 提供。

---

## 1. 读取配置

读取配置信息主要有两种方式：**手动调用 API** 和 **注解注入**。

### 方式一：手动调用 API (推荐)

这是**最推荐**的使用方式，尤其当你需要确保每次都获取到最新的配置值时。此方法直接从数据库查询，不受 Bean 单例缓存的影响。

首先，在你的服务中注入 `AdminConfigApiService`：

```java
import work.soho.admin.api.service.AdminConfigApiService;
import org.springframework.stereotype.Service;

@Service
public class MyBusinessService {

    private final AdminConfigApiService configApi;

    public MyBusinessService(AdminConfigApiService configApi) {
        this.configApi = configApi;
    }

    public void doSomething() {
        // 使用 getByKey 获取配置，如果不存在则使用默认值 "default-value"
        String myConfigValue = configApi.getByKey("my-feature-key", String.class, "default-value");

        if ("enabled".equals(myConfigValue)) {
            // 执行逻辑...
        }
    }
    
    public void processOrder() {
        // 获取一个配置并自动反序列化为 JSON 对象
        OrderConfig orderConfig = configApi.getByKey("order-processing-rules", OrderConfig.class);
        if (orderConfig != null && orderConfig.getTimeoutSeconds() > 0) {
            // ...
        }
    }
}
```

### 方式二：注解注入 (仅限静态配置)

你可以使用 `@Value` 注解结合 Spring Expression Language (SpEL) 来注入配置。

```java
@Service
public class MyStaticConfigService {

    @Value("#{@sohoConfig.getByKey('my-static-key')}")
    private String staticValue;

    // ...
}
```

**⚠️ 重要警告:**

-   此方法注入的值只在 Bean **首次创建时**从数据库获取一次。
-   由于 Bean 默认为单例（Singleton），在应用运行期间，即使数据库中的配置被修改，这个注入的值也**不会自动刷新**。
-   因此，此方法**仅适用于**那些在应用启动后就不会再改变的“静态”配置。
-   **请勿**为了解决缓存问题而将你的 Bean 设置为 `@Scope("prototype")`。这是一种错误的做法，会严重影响性能并可能导致意外的副作用。对于需要动态刷新值的场景，请使用上面推荐的“手动调用 API”方式。

---

## 2. 核心 API (`AdminConfigApiService`)

`AdminConfigApiService` 接口提供了以下核心方法来获取配置：

-   `<T> T getByKey(String key, Class<T> clazz)`
    -   **功能**: 根据 `key` 获取配置值，并尝试将其反序列化为指定的 `clazz` 类型对象。
    -   **注意**: 如果 `key` 不存在，此方法可能返回 `null`。如果配置值是 JSON 字符串，它将被转换为一个 `clazz` 实例。

-   `<T> T getByKey(String key, Class<T> clazz, T defaultValue)`
    -   **功能**: 功能同上，但如果 `key` 不存在或值为空，则安全地返回你提供的 `defaultValue`。这是更推荐的健壮用法。

---

## 3. 初始化模块配置

为了确保系统正常运行，每个业务模块都应该在启动时检查并初始化自己所需的配置项。这可以通过实现 `InitializingBean` 接口来完成。

该初始化过程是**幂等**的：系统会先检查配置项是否存在，只在不存在时才会创建，因此不会覆盖已有的线上修改。

### 初始化示例

以下示例展示了一个模块如何初始化自己的默认配置。

```java
import work.soho.admin.api.request.AdminConfigInitRequest;
import work.soho.admin.api.service.AdminConfigApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class MyModuleConfigInitializer implements InitializingBean {

    private static final String GROUP_KEY = "my-module-group";
    private static final String FEATURE_FLAG_KEY = "my-module-feature-enabled";

    private final AdminConfigApiService adminConfigApiService;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 1. 定义配置组
        ArrayList<AdminConfigInitRequest.Group> groups = new ArrayList<>();
        groups.add(AdminConfigInitRequest.Group.builder()
                .key(GROUP_KEY)
                .name("我的模块配置")
                .build());

        // 2. 定义配置项
        ArrayList<AdminConfigInitRequest.Item> items = new ArrayList<>();
        items.add(AdminConfigInitRequest.Item.builder()
                .key(FEATURE_FLAG_KEY)
                .groupKey(GROUP_KEY)
                .value("true") // 默认值
                .explain("用于控制我的模块某个功能的开关")
                .type(AdminConfigInitRequest.ItemType.RADIO.getType()) // 在UI中的显示类型
                .options("true:开启,false:关闭") // Radio/Select 类型的选项
                .build());

        // 3. 构建请求并调用初始化接口
        AdminConfigInitRequest initRequest = AdminConfigInitRequest.builder()
                .groupList(groups)
                .items(items)
                .build();
        
        adminConfigApiService.initItems(initRequest);
    }
}
```
