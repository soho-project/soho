package work.soho.admin.service.impl;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.api.admin.request.AdminConfigInitRequest;
import work.soho.test.TestApp;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("local")
@Log4j2
class AdminConfigServiceImplTest {

    @Autowired
    private AdminConfigServiceImpl adminConfigService;

    @Test
    void getByKey() {
    }

    @Test
    void testGetByKey() {
    }

    @Test
    void testGetByKey1() {
    }

    @Test
    void initItems() {
        AdminConfigInitRequest.Group group = AdminConfigInitRequest.Group.builder().name("test").key("test").build();
        ArrayList<AdminConfigInitRequest.Item> list = new ArrayList<>();

        AdminConfigInitRequest.Item item = AdminConfigInitRequest.Item.builder().build();
        list.add(item);
        item.setKey("test");
        item.setType(AdminConfigInitRequest.ItemType.TEXT.getType());
        item.setExplain("描述");
        item.setGroupKey(group.getKey());
        item.setValue("test");

        ArrayList<AdminConfigInitRequest.Group> groups = new ArrayList<>();
        groups.add(group);

        adminConfigService.initItems(AdminConfigInitRequest.builder().items(list).groupList(groups).build());
    }
}