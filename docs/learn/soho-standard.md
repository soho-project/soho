# SOHO 项目开发规范

本文档旨在统一项目开发过程中的各项标准与规范，以提高代码一致性、可读性和可维护性。

## 1. URL 路径规范

项目的 RESTful API URL 遵循一个四层结构，格式为：`/模块名/角色/控制器/方法`。

-   **模块名 (`module`)**: 业务模块的名称，应保持简洁且具有描述性。例如：`example`, `shop`, `user`。
-   **角色 (`role`)**: 定义该 API 的访问角色，决定了其基础的访问权限。
    -   `admin`: 管理后台接口，供管理员使用。
    -   `user`: 用户端接口，需要用户登录后才能访问。
    -   `guest`: 访客接口，无需登录即可访问，如登录、注册、公开内容等。
-   **控制器 (`controller`)**: 具体的资源或功能控制器名称，通常与业务实体相关。
-   **方法 (`method`)**: 控制器中具体的操作方法，对应增、删、查、改等具体功能。

**示例**:

-   `GET /shop/admin/product/page` - **商城模块**的**管理后台**，用于分页查询**商品**。
-   `POST /shop/user/order/create` - **商城模块**的**用户端**，用于**创建订单**。
-   `GET /shop/guest/product/latest` - **商城模块**的**访客端**，用于查看**最新商品**。

## 2. API 响应结构

所有 RESTful API 的响应都必须封装在统一的 `R<T>` 对象中，以确保前端能以一致的方式处理请求结果。该对象定义在 `soho-common-contract` 模块中。

**`R<T>` 对象结构**:

-   `code` (int): 业务状态码。例如 `2000` 代表成功，其他值代表各类错误。
-   `msg` (String): 给用户的提示信息，如 "success" 或具体的错误信息。
-   `payload` (T): 实际的业务数据。可以是单个对象、列表、分页信息等。

**Controller 方法示例**:

```java
@RestController
@RequestMapping("/example/admin/category")
public class ExampleCategoryController {

    @GetMapping("/get/{id}")
    public R<CategoryVo> getCategory(@PathVariable Long id) {
        CategoryVo category = categoryService.getById(id);
        // 使用 R.success(data) 包装成功响应
        return R.success(category);
    }
    
    @PostMapping("/create")
    public R<Void> createCategory(@RequestBody CategoryForm form) {
        boolean isSuccess = categoryService.create(form);
        if (isSuccess) {
            // 使用 R.success() 表示操作成功，无返回数据
            return R.success();
        } else {
            // 使用 R.error(msg) 返回错误信息
            return R.error("创建失败");
        }
    }
}
```

## 3. 包（Package）命名规范

项目遵循一个标准的包结构，以分离不同层级的代码。一个典型的业务模块 (`-biz` 后缀) 应包含以下包：

-   `work.soho.{module}.biz`
    -   `.AdminApplication` (启动类)
    -   `config`: 存放各类配置类 (如 MyBatis, Redis 配置)。
    -   `controller`: 存放 Spring MVC 控制器 (`@RestController`)。
    -   `domain`: 存放数据库实体类 (Entity)。
    -   `enums`: 存放枚举类。
    -   `mapper`: 存放 MyBatis Plus 的 Mapper 接口。
    -   `service`: 存放业务逻辑服务接口。
        -   `impl`: 存放服务接口的实现类。
    -   `vo`: 存放视图对象 (View Object)，用于 API 的输入和输出。
    -   `utils`: 存放工具类。
    -   `listener`: 存放事件监听器。

## 4. 日志规范

-   **实现**: 项目统一使用 `Log4j2` 作为日志框架。
-   **用法**: 推荐在类上使用 Lombok 的 `@Log4j2` 注解来自动注入一个名为 `log` 的静态日志记录器实例。

```java
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class MyService {

    public void doSomething(Long id) {
        log.info("开始处理业务，ID: {}", id);
        try {
            // ... 业务逻辑 ...
        } catch (Exception e) {
            log.error("处理业务时发生错误，ID: {}", id, e);
        }
    }
}
```

## 5. 代码风格与格式化

-   **标准**: 项目采用 `spring-javaformat` 作为统一的代码格式化标准。
-   **执行**: 在提交代码前，请务必在项目根目录下运行 Maven 命令来格式化你的代码，以确保所有代码风格一致。

    ```bash
    mvn spring-javaformat:apply
    ```
