package work.soho.shop.biz.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import work.soho.admin.api.request.AdminConfigInitRequest;
import work.soho.admin.api.service.AdminConfigApiService;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class ShopSysConfig implements InitializingBean {
    private final AdminConfigApiService adminConfigApiService;

    // 配置分组key
    private final String DEFAULT_SHOP_GROUP_NAME = "shop_config";

    // 首页轮播图
    public static final String INDEX_BANNER = "shop_index_banner";

    /**
     * 初始化配置项
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化组名
        ArrayList<AdminConfigInitRequest.Group> groups = new ArrayList<>();
        groups.add(AdminConfigInitRequest.Group.builder().key(DEFAULT_SHOP_GROUP_NAME).name("商城配置").build());
        // 构建单元配置
        ArrayList<AdminConfigInitRequest.Item> items = new ArrayList<>();
        items.add(AdminConfigInitRequest.Item.builder().groupKey(DEFAULT_SHOP_GROUP_NAME).key(INDEX_BANNER).value("").explain("首页轮播图")
                .type(AdminConfigInitRequest.ItemType.TEXT.getType()).build());
        adminConfigApiService.initItems(AdminConfigInitRequest.builder()
                .items(items).groupList( groups).build());
    }
}
