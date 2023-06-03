package work.soho.admin;

import cn.hutool.core.collection.ListUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.admin.domain.AdminResource;
import work.soho.admin.service.AdminResourceService;
import work.soho.admin.utils.TreeUtils;
import work.soho.common.core.util.HashMapUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = AdminApplication.class)
public class TreeTest {
    @Autowired
    private AdminResourceService adminResourceService;

    @Test
    public void tree() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ArrayList<AdminResource> list = (ArrayList<AdminResource>) adminResourceService.list();
        TreeUtils<Long, AdminResource> treeUtils = new TreeUtils();
        Class<?> c = AdminResource.class;


        treeUtils.loadData(list, c.getMethod("getId"), c.getMethod("getBreadcrumbParentId"));

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(2l);
        ids.add(19l);
        ids.add(27l);

        List<AdminResource> myList = treeUtils.getAllTreeNodeWidthIds(ids);
        HashMap<String, AdminResource> map = (HashMap<String, AdminResource>) HashMapUtils.fromList(myList, "route");
        System.out.println(myList);
        System.out.println("hello");
        System.out.println(map);
    }
}
