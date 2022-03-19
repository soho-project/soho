package work.soho.admin;

import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.annotation.Node;
import work.soho.admin.service.impl.AdminResourceServiceImpl;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = AdminApplication.class)
public class ClassTest {
    @Autowired
    AdminResourceServiceImpl adminResourceServiceImpl;

    @Test
    public void searchAnnotation() {
        getRequestMappingMethod("work");
    }

    public static void getRequestMappingMethod(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> classesList = reflections.getTypesAnnotatedWith(RestController.class);
        classesList.addAll(reflections.getTypesAnnotatedWith(Controller.class));
        System.out.println("==============================");
        System.out.println(classesList.size());
        for (Class classes : classesList) {
            System.out.println(classes.toString());
            //得到该类下面的所有方法
            Method[] methods = classes.getDeclaredMethods();
            for (Method method : methods) {
                //得到该类下面的RequestMapping注解
                Node node = method.getAnnotation(Node.class);
                if (null != node) {
                    System.out.println(node.value());

                }
            }
        }
    }


    @Test
    public void getgMethod2() {
        Reflections reflections = new Reflections("work.soho", Scanners.MethodsAnnotated);
        Set<Method> methods = reflections.getMethodsAnnotatedWith(Node.class);

        System.out.println("==============================");
        ArrayList<Node> arrayList = new ArrayList<>();
        for (Method method : methods) {
            Node node = method.getAnnotation(Node.class);
            if (null != node) {
                System.out.println(node.value());
                arrayList.add(node);
            }
        }
    }

    @Test
    public void syncTest() {
        adminResourceServiceImpl.syncResource2Db();
    }

    @Test
    public void passwordTest() {
        String password = new BCryptPasswordEncoder().encode("123456");
        System.out.println(password);
    }
}
