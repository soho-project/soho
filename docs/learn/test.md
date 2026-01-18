# 单元测试

本文档旨在提供项目单元测试的编写指南，帮助开发者快速上手。项目主要采用 `Spring Boot Test`、`JUnit 5` 和 `Mockito` 作为核心测试框架。

## 核心测试依赖

为了统一管理和复用测试相关的功能，项目提供了 `soho-common-test` 模块。在你的模块中，请确保已在 `pom.xml` 中添加此依赖项：

```xml
<dependency>
    <groupId>work.soho</groupId>
    <artifactId>soho-common-test</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

该模块封装了测试所需的基础环境、工具类以及模拟认证等功能。

## Controller / API 接口测试

对于 Controller 层的 RESTful API，我们使用 `MockMvc` 进行集成测试。这种方式可以让我们在不启动完整 Web 服务器的情况下，对 API 端点进行全面的请求和响应验证。

### 关键注解

-   `@SpringBootTest(classes = ...)`: 加载完整的 Spring 应用上下文进行测试，`classes` 属性通常指向启动类（例如 `AdminApplication.class` 或测试专用的 `TestApp.class`）。
-   `@WebAppConfiguration`: 启用 Web 应用上下文测试。
-   `@ContextConfiguration`: 用于指定上下文配置。

### 模拟认证用户

在需要用户登录才能访问的接口测试中，我们不必手动处理登录逻辑。项目提供了 `@SohoWithUser` 注解来轻松模拟一个已认证的用户。

`@SohoWithUser` 注解可以直接用于测试方法或测试类上。

**注解参数说明：**

-   `id` (或 `value`): 模拟用户的 ID，默认为 `1L`。
-   `username`: 模拟用户的用户名，默认为 `"test"`。
-   `role`: 模拟用户的角色，默认为 `"admin"`。
-   `password`: 模拟用户的密码，默认为 `"password"`。

**使用示例：**

```java
@Test
// 模拟一个 ID=6, 用户名=admin, 角色=admin 的用户
@SohoWithUser(id = 6, username = "admin",  role = "admin")
void someAuthenticatedEndpointTest() throws Exception {
    mockMvc.perform(get("/api/some/secure/resource"))
           .andExpect(status().isOk());
}
```

### 完整示例

以下是一个测试文件上传接口的完整示例，展示了如何组织一个 Controller 测试类。

`ExampleControllerTest.java`:
```java
package work.soho.example.biz.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import work.soho.test.TestApp;
import work.soho.test.security.support.SohoWithUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
class ExampleControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        // 在每个测试方法执行前，构建 MockMvc 实例
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    // 使用 @SohoWithUser 模拟一个已登录的管理员用户
    @SohoWithUser(id = 6, username = "admin",  role = "admin")
    void importExcel() throws Exception {
        // 1. 准备测试文件
        ClassPathResource resource = new ClassPathResource("example2.xls");
        MockMultipartFile file = new MockMultipartFile(
                "file", // 这个名称必须和 Controller 方法中 @RequestParam("file") 的名称一致
                "example.xls",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                resource.getInputStream()
        );

        // 2. 执行 POST 请求并携带文件
        mockMvc.perform(multipart("/admin/example/importExcel").file(file))
                .andDo(print()) // 打印请求和响应的详细信息
                .andExpect(status().isOk()); // 3. 断言响应状态码为 200 OK
    }
}
```

## Service 服务层测试

服务层测试专注于业务逻辑。与 Controller 测试类似，它也通过 `@SpringBootTest` 加载应用上下文。但通常我们不需要 Web 环境。

如果被测试的 Service 依赖于其他 Bean（例如一个 Mapper 或另一个 Service），你可以使用 `@MockBean` 来模拟这些依赖的行为，从而实现对目标 Service 的隔离测试。

### `@MockBean` 示例

```java
@SpringBootTest
class MyServiceTest {

    @Autowired
    private MyService myService; // 被测试的服务

    @MockBean
    private OtherService otherService; // 需要被模拟的依赖

    @MockBean
    private SomeMapper someMapper; // 需要被模拟的 Mapper

    @Test
    void testSomething() {
        // 1. 定义模拟行为 (Arrange)
        // 当 otherService.doSomething() 被调用时，返回 "mocked-value"
        when(otherService.doSomething()).thenReturn("mocked-value");
        // 当 someMapper.findById(1L) 被调用时，返回一个 User 对象
        when(someMapper.findById(1L)).thenReturn(new User("test-user"));

        // 2. 执行业务逻辑 (Act)
        String result = myService.process(1L);

        // 3. 断言结果 (Assert)
        assertThat(result).isEqualTo("expected-result-based-on-mocks");

        // 4. (可选) 验证模拟对象的方法是否被按预期调用
        verify(otherService, times(1)).doSomething();
    }
}
```

## 运行测试

你可以通过以下方式运行测试：

1.  **IDE**: 在测试类或测试方法上右键，选择 "Run"。
2.  **Maven**: 在项目根目录下运行 `mvn test` 命令。