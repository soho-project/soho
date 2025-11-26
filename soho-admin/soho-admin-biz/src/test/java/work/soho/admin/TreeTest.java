package work.soho.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.admin.biz.AdminApplication;
import work.soho.admin.biz.domain.AdminResource;
import work.soho.admin.biz.service.AdminResourceService;
import work.soho.common.core.util.TreeUtils;
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
        ids.add(2L);
        ids.add(19L);
        ids.add(29L);

        List<AdminResource> myList = treeUtils.getAllTreeNodeWidthIds(ids);
        HashMap<String, AdminResource> map = (HashMap<String, AdminResource>) HashMapUtils.fromList(myList, "route");

        List<AdminResource> parentList = treeUtils.getAllParentByIds(ids);

        System.out.println(myList);
        System.out.println("hello");
        System.out.println(map);
        System.out.println("===================");
        System.out.println(parentList);
    }
}
