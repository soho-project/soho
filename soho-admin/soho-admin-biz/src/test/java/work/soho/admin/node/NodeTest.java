package work.soho.admin.node;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.AdminApplication;
import work.soho.admin.domain.AdminResource;
import work.soho.admin.service.AdminResourceService;
import work.soho.api.admin.annotation.Node;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = AdminApplication.class)
public class NodeTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AdminResourceService adminResourceService;

    @Test
    public void testGetNodeAnnotation() {
        String resourceRole = "resource_role";
        AdminResource params = new AdminResource();
        params.setRemarks(resourceRole);
        adminResourceService.remove(Wrappers.lambdaQuery(params));

        List<AdminResource> resourceList = adminResourceService.list(
                Wrappers.lambdaQuery(new AdminResource())
                        .ne(AdminResource::getBreadcrumbParentId, 1)
        );
        Map<String, Long> resourceMap = resourceList.stream().collect(Collectors.toMap(e -> StringUtils.replaceEach(e.getRoute(), new String[]{"_", "/"}, new String[]{"", ""}), AdminResource::getId, (v1, v2) -> v1));

        Map<String, Object> restControllerBeanMap = applicationContext.getBeansWithAnnotation(RestController.class);

        List<AdminResource> data = new ArrayList<>();
        // 获取所有方法上面@Node
        restControllerBeanMap.forEach((key, value) -> {
            Class<?> targetClass = AopUtils.getTargetClass(value);
            Method[] methods = targetClass.getDeclaredMethods();
            for (Method method : methods) {
                Node node = AnnotationUtils.findAnnotation(method, Node.class);
                if (node == null) {
                    continue;
                }

                String val = node.value();
                String k = StringUtils.lowerCase(StringUtils.substringBefore(val, ":"));
                Long id = resourceMap.get(k);

                if (id == null) {
                    continue;
                }

                AdminResource adminResource = new AdminResource();
                adminResource.setBreadcrumbParentId(id);
                adminResource.setRoute(val);
                adminResource.setType(2);
                adminResource.setVisible(0);
                adminResource.setRemarks(resourceRole);

                String code = StringUtils.substringAfterLast(val, ":");
                OptionsEnum em = OptionsEnum.match(code);

                adminResource.setName(code);
                adminResource.setZhName(em.getDesc());

                data.add(adminResource);
            }
        });

        adminResourceService.saveBatch(data);
    }
}
