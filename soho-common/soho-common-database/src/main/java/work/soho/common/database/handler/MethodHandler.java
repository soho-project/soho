package work.soho.common.database.handler;

import lombok.Data;

import java.lang.reflect.Method;

@Data
public class MethodHandler {
    private final Object targetBean;
    private final Method method;
    private final int order;
    private final boolean async;
}