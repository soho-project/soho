package work.soho.wallet.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import work.soho.wallet.api.service.WalletUserApiService;
import work.soho.wallet.biz.domain.WalletUser;
import work.soho.wallet.biz.mapper.WalletUserMapper;
import work.soho.wallet.biz.service.WalletUserService;

@RequiredArgsConstructor
@Service
public class WalletUserServiceImpl extends ServiceImpl<WalletUserMapper, WalletUser>
    implements WalletUserService, WalletUserApiService {

    /**
     * 验证支付密码
     *
     * @param userId
     * @param payPassword
     * @return
     */
    @Override
    public Boolean verificationPayPassword(Long userId, String payPassword) {
        WalletUser walletUser = getById(userId);
        if (walletUser != null) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            // 验证密码
            String password = walletUser.getPayPassword();
            return encoder.matches(payPassword, password);
        }
        return false;
    }

}