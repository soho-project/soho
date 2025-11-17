package work.soho.wallet.biz.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.common.core.result.R;
import work.soho.common.security.annotation.Node;
import work.soho.wallet.biz.domain.WalletUser;
import work.soho.wallet.biz.service.WalletUserService;

;
/**
 * 钱包用户Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet/user/walletUser" )
public class UserWalletUserController {

    private final WalletUserService walletUserService;

    /**
     * 新增钱包用户
     */
    @PostMapping
    @Node(value = "user::walletUser::add", name = "新增 钱包用户")
    public R<Boolean> add(@RequestBody WalletUser walletUser) {
        return R.success(walletUserService.save(walletUser));
    }

    /**
     * 修改钱包用户
     */
    @PutMapping
    @Node(value = "user::walletUser::edit", name = "修改 钱包用户")
    public R<Boolean> edit(@RequestBody WalletUser walletUser) {
        return R.success(walletUserService.updateById(walletUser));
    }


}