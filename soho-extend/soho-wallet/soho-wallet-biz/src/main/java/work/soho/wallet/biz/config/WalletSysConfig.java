package work.soho.wallet.biz.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import work.soho.admin.api.request.AdminConfigInitRequest;
import work.soho.admin.api.service.AdminConfigApiService;

import java.math.BigDecimal;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class WalletSysConfig implements InitializingBean {
    private final AdminConfigApiService adminConfigApiService;

    private final String DEFAULT_GROUP_NAME = "wallet";
    // 茸元钱包单日最大转账比例
    private static final String WALLET_RY_TRANSFER_DAY_MAX_RATE_KEY = "wallet_ry_transfer_day_max_rate";

    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化组名
        ArrayList<AdminConfigInitRequest.Group> groups = new ArrayList<>();
        groups.add(AdminConfigInitRequest.Group.builder().key(DEFAULT_GROUP_NAME).name("钱包配置").build());
        ArrayList<AdminConfigInitRequest.Item> items = new ArrayList<>();

        items.add(AdminConfigInitRequest.Item.builder().groupKey(DEFAULT_GROUP_NAME).key(WALLET_RY_TRANSFER_DAY_MAX_RATE_KEY).value("0.3").type(AdminConfigInitRequest.ItemType.TEXT.getType()).explain("茸元钱包单日最大转账比例").build());

        adminConfigApiService.initItems(AdminConfigInitRequest.builder().items(items).groupList(groups).build());
    }

    public BigDecimal getWalletRyTransferDayMaxRate() {
        return adminConfigApiService.getByKey(WALLET_RY_TRANSFER_DAY_MAX_RATE_KEY, BigDecimal.class, new BigDecimal("0.3"));
    }
}
