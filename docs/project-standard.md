项目规范
=======

编码
----

> 文件编码，使用utf-8编码； 
> 
> 配置运行jvm 参数:  -Dfile.encoding=UTF-8

注入相关
-------

> > 依赖注入使用构造注入 <br>

```java
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class A {
    private final class B

    ;
}
```

> 禁止使用sout，打印使用log <br>

```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class A {

    public void test() {
        log.info("xxx");
    }
}
```

> 代码审查使用sonarlint <br>

```text
idea插件自行下载
```

> 版本控制使用父POM <br>
> service一定要写接口
> 可通用的配置写common


类注释模板

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

Api

http://localhost:8080/api-docs/swagger-ui/

```text
|--soho
    |--soho-admin
        |--soho-admin-api (实体类存放，之后feign也放在这里)
        |--soho-admin-biz (springboot服务)
    |--soho-common
        |--soho-common-bom (版本管理)
        |--soho-common-core (核心包)
        |--soho-common-security (鉴权依赖包)
        、、、(common包待补充)
```

Action

    @Node(value = "unique-key", visible = 1, describe = "describe")

@Log 对方法调用进行日志记载

    @Log("log key word")

URL路由命名规范
=============


    /[角色名]/[业务模块名]/[Controller Name]/[业务名；可选]


- 角色名

  用来鉴权的角色；例如 admin, chat, user

- 业务模块名

  例如： chat, code, content

- 控制器名

  一般控制器名称同业务紧密相关
****
