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

    // 首页动作图标
    public static final String INDEX_ACTIONS = "shop_index_actions";

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

        items.add(AdminConfigInitRequest.Item.builder().groupKey(DEFAULT_SHOP_GROUP_NAME).key(INDEX_ACTIONS).value("[\n" +
                        "    {\n" +
                        "      id: 1,\n" +
                        "      name: '手机数码',\n" +
                        "      icon: 'https://img14.360buyimg.com/n5/s720x720_jfs/t1/340823/19/8979/22172/68c099fdFd52c4494/e1c46bc4036a19ca.jpg.avif'\n" +
                        "    },\n" +
                        "    {\n" +
                        "      id: 2,\n" +
                        "      name: '电脑办公',\n" +
                        "      icon: 'https://img13.360buyimg.com/n5/s720x720_jfs/t1/332522/6/21256/171759/68e8a8b3Fbe290999/0cf9f1e3e732ddfc.jpg.avif'\n" +
                        "    },\n" +
                        "    {\n" +
                        "      id: 3,\n" +
                        "      name: '家用电器',\n" +
                        "      icon: 'https://img12.360buyimg.com/jdphoto/s120x120_jfs/t1/17169/3/4127/4611/5c2f35cfEd87b0dd5/65c0cdc1362635fc.png'\n" +
                        "    },\n" +
                        "    {\n" +
                        "      id: 4,\n" +
                        "      name: '服装鞋包',\n" +
                        "      icon: 'https://img12.360buyimg.com/jdphoto/s120x120_jfs/t17725/156/1767366877/17404/f45d418b/5ad87bf0N4c5bcb5c.jpg'\n" +
                        "    },\n" +
                        "    {\n" +
                        "      id: 5,\n" +
                        "      name: '食品生鲜',\n" +
                        "      icon: 'https://img12.360buyimg.com/jdphoto/s120x120_jfs/t1/70608/1/2597/5080/5d0b1d51E08e5d0ac/6886e106c2e2c4f0.png'\n" +
                        "    },\n" +
                        "    {\n" +
                        "      id: 6,\n" +
                        "      name: '美妆护肤',\n" +
                        "      icon: 'https://img12.360buyimg.com/jdphoto/s120x120_jfs/t1/44882/32/14628/3727/5db6fa4cE0b5d1ef8/899664aaa3f5b5fe.png'\n" +
                        "    },\n" +
                        "    {\n" +
                        "      id: 7,\n" +
                        "      name: '母婴玩具',\n" +
                        "      icon: 'https://img12.360buyimg.com/jdphoto/s120x120_jfs/t1/25410/20/10674/4760/5c8614daE667c9810/51d9e01a428e02a4.png'\n" +
                        "    },\n" +
                        "    {\n" +
                        "      id: 8,\n" +
                        "      name: '家居家装',\n" +
                        "      icon: 'https://img12.360buyimg.com/jdphoto/s120x120_jfs/t1/27766/19/10874/4636/5c8614daE667c9810/51d9e01a428e02a4.png'\n" +
                        "    }\n" +
                        "  ]").explain("首页动作列表")
                .type(AdminConfigInitRequest.ItemType.JSON.getType()).build());

        adminConfigApiService.initItems(AdminConfigInitRequest.builder()
                .items(items).groupList( groups).build());
    }

    /**
     * 获取首页轮播图
     *
     * @return
     */
    public String getIndexBanner() {
        return adminConfigApiService.getByKey(INDEX_BANNER, String.class, "");
    }

    /**
     * 获取首页动作图标
     *
     * @return
     */
    public String getIndexActions() {
        return adminConfigApiService.getByKey(INDEX_ACTIONS, String.class, "");
    }
}
