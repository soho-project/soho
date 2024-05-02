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

    private final AdminConfigApiService adminConfigApiService;

    /**
     * 获取默认数据库名
     *
     * @return
     */
    public String getDefaultDbName() {
        return adminConfigApiService.getByKey(DEFAULT_DB_NAME_KEY, String.class, DEFAULT_DB_NAME);
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
        adminConfigApiService.initItems(AdminConfigInitRequest.builder().items(items).groupList(groups).build());
    }
}
