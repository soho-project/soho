package work.soho.common.data.lock.aspect;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import work.soho.common.data.lock.annotation.Lock;
import work.soho.common.data.lock.exception.LockException;
import work.soho.common.data.lock.utils.LockUtils;

import java.lang.reflect.Method;

@Log4j2
@Aspect
@Component
@Configuration
public class LockAspect {
    /**
     * 用于SpEL表达式解析.
     */
    private final SpelExpressionParser parser = new SpelExpressionParser();
    /**
     * 用于获取方法参数定义名字.
     */
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();


    /**
     * 加锁环绕
     *
     * @param invocation
     * @return
     */
    @Around(value = "@annotation(work.soho.common.data.lock.annotation.Lock)")
    public Object around(ProceedingJoinPoint invocation) {
        Lock lock = getLock(invocation);
        String lockKey = generateKeyBySpEL(lock.value(), invocation);
        System.out.println(lockKey);
        System.out.println("===================================================");
        LockUtils.getLockClient().getLock(lockKey).lock();
        try{
            Object result = invocation.proceed();
            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            LockUtils.getLockClient().getLock(lockKey).unlock();
        }
    }

    /**
     * 获取方法上的Lock注解
     *
     * @param invocation
     * @return
     */
    private Lock getLock(ProceedingJoinPoint invocation) {
        String methodName=invocation.getSignature().getName();
        Class<?> classTarget=invocation.getTarget().getClass();
        Class<?>[] par=((MethodSignature) invocation.getSignature()).getParameterTypes();
        try {
            Method objMethod=classTarget.getMethod(methodName, par);
            return objMethod.getAnnotation(Lock.class);
        } catch (Exception e) {
            throw new LockException("lock exception: " + e.getMessage());
        }
    }

    private String generateKeyBySpEL(String spELString, JoinPoint joinPoint) {
        //判断是否为spel
        if(!spELString.contains("#")) {
            return spELString;
        }

        // 通过joinPoint获取被注解方法
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        // 使用spring的DefaultParameterNameDiscoverer获取方法形参名数组
        String[] paramNames = nameDiscoverer.getParameterNames(method);
        // 解析过后的Spring表达式对象
        Expression expression = parser.parseExpression(spELString);
        // spring的表达式上下文对象
        EvaluationContext context = new StandardEvaluationContext();
        // 通过joinPoint获取被注解方法的形参
        Object[] args = joinPoint.getArgs();
        // 给上下文赋值
        for(int i = 0 ; i < args.length ; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        // 表达式从上下文中计算出实际参数值
        /*如:
            @annotation(key="#student.name")
             method(Student student)
             那么就可以解析出方法形参的某属性值，return “xiaoming”;
          */
        return expression.getValue(context).toString();
    }
}
