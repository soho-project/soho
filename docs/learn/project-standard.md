# 项目规范

本文档整理项目中的基础开发约束，适合作为日常编码和代码审查的最低标准。

## 编码与运行环境

- 文件编码统一使用 `UTF-8`
- 建议配置 JVM 参数：`-Dfile.encoding=UTF-8`

## 依赖注入

- 优先使用构造注入
- 推荐配合 Lombok 的 `@RequiredArgsConstructor`

```java
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class A {
    private final B b;
}
```

## 日志规范

- 禁止使用 `System.out.println`
- 统一使用日志组件输出运行信息

```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class A {
    public void test() {
        log.info("xxx");
    }
}
```

## 开发约束

- 代码审查建议启用 SonarLint
- 版本控制统一由父 `POM` 管理
- `service` 层建议提供接口
- 可复用配置优先沉淀到 `common` 模块

## 类注释模板

```java
/**
 * <p>
 * ${NAME}
 * </p>
 *
 * @author fang
 * @date ${DATE}
 */
```

## API 文档

- Swagger UI: <http://localhost:6677/swagger-ui/index.html>
- Knife4j: <http://localhost:6677/doc.html>

关闭 Swagger：

```properties
springfox.documentation.enabled=false
```

## Knife4j 调试脚本示例

```javascript
// 参考文档 https://gitee.com/xiaoym/knife4j/wikis/AfterScript
var code = ke.response.data.code;
if (code == 2000) {
  var token = ke.response.data.payload.token;
  ke.global.setHeader("Authorization", token);
}
```

## 项目结构示例

```text
|-- soho
    |-- soho-admin
        |-- soho-admin-api
        |-- soho-admin-biz
    |-- soho-common
        |-- soho-common-bom
        |-- soho-common-core
        |-- soho-common-security
```

说明：

- `soho-admin-api`：公共实体、接口定义与跨模块调用契约
- `soho-admin-biz`：Spring Boot 业务实现
- `soho-common-*`：通用基础能力

## 权限与日志注解

资源节点注解：

```java
@Node(value = "unique-key", name = "describe")
```

操作日志注解：

```java
@Log("log key word")
```

## URL 路由命名规范

统一采用以下结构：

```text
/[业务模块名]/[角色名]/[Controller Name]/[业务名; 可选]
```

说明：

- 角色名：如 `admin`、`chat`、`user`
- 业务模块名：如 `chat`、`content`、`shop`
- 控制器名：应与业务职责保持一致
