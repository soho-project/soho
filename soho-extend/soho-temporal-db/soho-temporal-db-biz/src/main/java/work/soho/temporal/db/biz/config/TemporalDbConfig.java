package work.soho.temporal.db.biz.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import work.soho.api.admin.request.AdminConfigInitRequest;
import work.soho.api.admin.service.AdminConfigApiService;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class TemporalDbConfig implements InitializingBean {
    private final String DEFAULT_DB_NAME_KEY = "default_db_name_key";
    private final String DEFAULT_DB_NAME = "root.soho";
    private final String DEFAULT_GROUP_NAME = "temporal_db";

    private final String DB_HOST = "127.0.0.1";
    private final String DB_HOST_KEY = "temporal_db_host";
    private final Integer DB_PORT = 6667;
    private final String DB_PORT_KEY = "temporal_db_port";
    private final String DB_USERNAME = "root";
    private final String DB_USERNAME_KEY = "temporal_db_username";
    private final String DB_PASSWORD = "root";
    private final String DB_PASSWORD_KEY = "temporal_db_password";

    private final AdminConfigApiService adminConfigApiService;

    /**
     * 获取默认数据库名
     *
     * @return
     */
    public String getDefaultDbName() {
        return adminConfigApiService.getByKey(DEFAULT_DB_NAME_KEY, String.class, DEFAULT_DB_NAME);
    }

    public String getDbHost() {
        return adminConfigApiService.getByKey(DB_HOST_KEY, String.class, DB_HOST);
    }

    public Integer getDbPort() {
        return adminConfigApiService.getByKey(DB_PORT_KEY, Integer.class, DB_PORT);
    }

    public String getDbUsername() {
        return adminConfigApiService.getByKey(DB_USERNAME_KEY, String.class, DB_USERNAME);
    }

    public String getDbPassword() {
        return adminConfigApiService.getByKey(DB_PASSWORD_KEY, String.class, DB_PASSWORD);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ArrayList<AdminConfigInitRequest.Group> groups = new ArrayList<>();
        groups.add(AdminConfigInitRequest.Group.builder().key(DEFAULT_GROUP_NAME).name("时序数据库").build());

        AdminConfigInitRequest.Item item = AdminConfigInitRequest.Item.builder()
                .key(DEFAULT_DB_NAME_KEY)
                .groupKey(DEFAULT_GROUP_NAME)
                .value(DEFAULT_DB_NAME)
                .explain("默认时序数据库名")
                .type(AdminConfigInitRequest.ItemType.TEXT.getType())
                .build();
        ArrayList<AdminConfigInitRequest.Item> items = new ArrayList<>();
        items.add(item);
        items.add(AdminConfigInitRequest.Item.builder().groupKey(DEFAULT_GROUP_NAME).key(DB_HOST_KEY).value(DB_HOST).type(AdminConfigInitRequest.ItemType.TEXT.getType()).explain("时序数据库主机").build());
        items.add(AdminConfigInitRequest.Item.builder().groupKey(DEFAULT_GROUP_NAME).key(DB_PORT_KEY).value(String.valueOf(DB_PORT)).type(AdminConfigInitRequest.ItemType.TEXT.getType()).explain("时序数据库端口号").build());
        items.add(AdminConfigInitRequest.Item.builder().groupKey(DEFAULT_GROUP_NAME).key(DB_USERNAME_KEY).value(DB_USERNAME).type(AdminConfigInitRequest.ItemType.TEXT.getType()).explain("时序数据库用户名").build());
        items.add(AdminConfigInitRequest.Item.builder().groupKey(DEFAULT_GROUP_NAME).key(DB_PASSWORD_KEY).value(DB_PASSWORD).type(AdminConfigInitRequest.ItemType.TEXT.getType()).explain("时序数据库密码").build());
        adminConfigApiService.initItems(AdminConfigInitRequest.builder().items(items).groupList(groups).build());
    }
}
