package work.soho.wallet.biz.service;

import work.soho.wallet.biz.domain.WalletUser;
import com.baomidou.mybatisplus.extension.service.IService;

public interface WalletUserService extends IService<WalletUser> {
    // 验证密码
    Boolean verificationPayPassword(Integer userId, String payPassword);
}