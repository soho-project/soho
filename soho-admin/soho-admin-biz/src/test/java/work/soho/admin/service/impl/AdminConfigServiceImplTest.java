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
        AdminConfigInitRequest.Group group = new AdminConfigInitRequest.Group();
        group.setName("test");
        group.setKey("test");

        ArrayList<AdminConfigInitRequest.Item> list = new ArrayList<>();

        AdminConfigInitRequest.Item item = new AdminConfigInitRequest.Item();
        list.add(item);
        item.setKey("test");
        item.setType(AdminConfigInitRequest.ItemType.TEXT.getType());
        item.setExplain("描述");
        item.setGroupKey(group.getKey());
        item.setValue("test");

        AdminConfigInitRequest adminConfigInitRequest = new AdminConfigInitRequest();
        adminConfigInitRequest.setGroup(group);
        adminConfigInitRequest.setItems(list);
        adminConfigService.initItems(adminConfigInitRequest);
    }
}