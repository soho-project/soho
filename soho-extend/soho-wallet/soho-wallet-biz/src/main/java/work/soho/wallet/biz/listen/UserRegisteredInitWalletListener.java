package work.soho.wallet.biz.listen;

import com.baomidou.dynamic.datasource.annotation.DsTxEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import work.soho.user.api.event.UserRegisteredEvent;
import work.soho.wallet.biz.domain.WalletType;
import work.soho.wallet.biz.enums.WalletTypeEnums;
import work.soho.wallet.biz.service.WalletInfoService;
import work.soho.wallet.biz.service.WalletTypeService;

import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class UserRegisteredInitWalletListener {
    private final WalletTypeService walletTypeService;
    private final WalletInfoService walletInfoService;

    @DsTxEventListener
    public void onUserRegistered(UserRegisteredEvent event) {
        if (event == null || event.getUserId() == null) {
            return;
        }

        List<WalletType> walletTypes = walletTypeService.list(
                new LambdaQueryWrapper<WalletType>()
                        .eq(WalletType::getStatus, WalletTypeEnums.Status.ACTIVE.getId())
        );
        for (WalletType walletType : walletTypes) {
            walletInfoService.getByUserIdAndType(event.getUserId(), walletType.getId());
        }
        log.info("init user wallets on register, userId={}, walletTypeCount={}", event.getUserId(), walletTypes.size());
    }
}
