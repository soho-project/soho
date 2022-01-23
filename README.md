> 依赖注入使用构造注入 <br>

```java
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class A {
    private final class B;
}
```
> 禁止使用sout，打印使用log <br>

```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class A {

    public void test(){
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
 * @author livk
 * @date ${DATE}
 */
```
