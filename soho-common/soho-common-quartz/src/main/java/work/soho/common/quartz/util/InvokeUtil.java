package work.soho.common.quartz.util;

import cn.hutool.extra.spring.SpringUtil;
import lombok.experimental.UtilityClass;
import work.soho.common.core.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

@UtilityClass
public class InvokeUtil {
    /**
     * 调用job指令
     *
     * @param cmd
     */
    public void invoke(String cmd) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException {
        //todo 添加job排它锁

        String className = getClassName(cmd);
        String methodName = getMethodName(cmd);
        List<Object[]> methodParams = getParams(cmd);
        //获取bean
        if(className.indexOf('.')>0) {
            //完整类名方式获取bean
            invoke(SpringUtil.getBean(Class.forName(className)), methodName, methodParams);
        } else {
            //bean name方式获取 bean
            invoke(SpringUtil.getBean(className), methodName, methodParams);
        }
    }

    private void invoke(Object bean, String methodName, List<Object[]> methodParams)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException
    {
        if (methodParams!=null && methodParams.size() > 0) {
            Method method = bean.getClass().getDeclaredMethod(methodName, getMethodParamsType(methodParams));
            method.invoke(bean, getMethodParamsValue(methodParams));
        } else {
            Method method = bean.getClass().getDeclaredMethod(methodName);
            method.invoke(bean);
        }
    }

    public static Class<?>[] getMethodParamsType(List<Object[]> methodParams) {
        Class<?>[] classs = new Class<?>[methodParams.size()];
        int index = 0;
        for (Object[] os : methodParams) {
            classs[index] = (Class<?>) os[1];
            index++;
        }
        return classs;
    }

    public static Object[] getMethodParamsValue(List<Object[]> methodParams) {
        Object[] classs = new Object[methodParams.size()];
        int index = 0;
        for (Object[] os : methodParams) {
            classs[index] = (Object) os[0];
            index++;
        }
        return classs;
    }

    /**
     * 获取类名/bean name
     *
     * @param cmd
     * @return
     */
    private String getClassName(String cmd) {
        String[] tmpParts = cmd.split("::");
        if(tmpParts.length != 2) {
            throw new RuntimeException("为正确识别指令");
        }
        return tmpParts[0];
    }

    /**
     * 获取方法名
     *
     * @param cmd
     * @return
     */
    private String getMethodName(String cmd) {
        String[] tmpParts = cmd.split("::");
        if(tmpParts.length != 2) {
            throw new RuntimeException("为正确识别指令");
        }
        String[] methodParts = tmpParts[1].split("\\(");
        if(methodParts.length != 2) {
            throw new RuntimeException("格式不正确， 请检查格式");
        }
        return methodParts[0];
    }

    /**
     * 获取命令行参数
     *
     * @param cmd
     * @return
     */
    private List<Object[]> getParams(String cmd) {
        List<Object[]> params = new LinkedList<>();
        int start = cmd.indexOf('(');
        int end = cmd.indexOf(')');
        String paramsStr = cmd.substring(start+1, end);
        String[] parmasPart = paramsStr.split(",");
        for (int i = 0; i < parmasPart.length; i++) {
            String item = parmasPart[i].trim();
            System.out.println(item);
            if(item.startsWith("'") || item.startsWith("\"")) { //字符串
                params.add(new Object[]{item.substring(1, item.length()-1), String.class});
            } else if("true".equalsIgnoreCase(item) || "false".equalsIgnoreCase(item)) {
                params.add(new Object[]{Boolean.valueOf(item), Boolean.class});
            } else if(item.endsWith("L")) {
                params.add(new Object[]{Long.valueOf(item.substring(0, item.length()-1)), Long.class});
            } else if(item.endsWith("D")) {
                params.add(new Object[]{Double.valueOf(item.substring(0, item.length()-1)), Double.class});
            } else {
                if(StringUtils.isNotBlank(item)) {
                    params.add(new Object[]{Integer.valueOf(item), Integer.class});
                }
            }
        }
        return params;
    }
}
