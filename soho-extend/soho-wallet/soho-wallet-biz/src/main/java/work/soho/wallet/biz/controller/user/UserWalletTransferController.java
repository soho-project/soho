package work.soho.wallet.biz.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.user.api.dto.UserInfoDto;
import work.soho.user.api.service.UserApiService;
import work.soho.wallet.api.vo.WalletTransferVo;
import work.soho.wallet.biz.domain.WalletInfo;
import work.soho.wallet.biz.domain.WalletTransfer;
import work.soho.wallet.biz.service.WalletInfoService;
import work.soho.wallet.biz.service.WalletTransferService;
import work.soho.wallet.biz.service.WalletUserService;

import java.util.List;
/**
 * 钱包转账Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet/user/walletTransfer" )
public class UserWalletTransferController {

    private final WalletTransferService walletTransferService;
    private final WalletUserService walletUserService;
    private final WalletInfoService walletInfoService;
    private final UserApiService userApiService;

    /**
     * 转账接口
     *
     * @param sohoUserDetails
     * @param walletTransferVo
     * @return
     */
    @PostMapping("/transfer")
    @ApiOperation(value = "转账接口")
    public R<WalletTransfer> transfer(@AuthenticationPrincipal SohoUserDetails sohoUserDetails,
                                      @RequestBody WalletTransferVo walletTransferVo)
    {
        try {
            Assert.isTrue(walletTransferVo.getFromWalletId() != null
                            && (walletTransferVo.getToWalletId() != null
                            || (walletTransferVo.getToPhone() != null && walletTransferVo.getToWalletType() != null)),
                    "请正确传递参数");
            // 确认目标钱包ID
            if(walletTransferVo.getToWalletId() == null) {
                // 根据手机号码查询用户信息
                UserInfoDto userInfoDto = userApiService.getUserInfoByPhone(walletTransferVo.getToPhone());
                Assert.notNull(userInfoDto, "目标用户不存在");
                WalletInfo toWalletInfo = walletInfoService.getByUserIdAndType(userInfoDto.getId(), walletTransferVo.getToWalletType());
                Assert.notNull(toWalletInfo, "目标钱包不存在");
                walletTransferVo.setToWalletId(toWalletInfo.getId());
            }

            if(!walletUserService.verificationPayPassword(sohoUserDetails.getId(), walletTransferVo.getPayPassword())) {
                return R.error("支付密码错误");
            }

            // 执行转账
            WalletTransfer walletTransfer = walletTransferService.transfer(
                    sohoUserDetails.getId(),
                    walletTransferVo.getFromWalletId(),
                    walletTransferVo.getToWalletId(),
                    walletTransferVo.getAmount(),
                    walletTransferVo.getRemark()
            );
            return R.success(walletTransfer);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
    }


    /**
     * 查询钱包转账列表
     */
    @GetMapping("/list")
    @Node(value = "user::walletTransfer::list", name = "获取 钱包转账 列表")
    public R<PageSerializable<WalletTransfer>> list(WalletTransfer walletTransfer, BetweenCreatedTimeRequest betweenCreatedTimeRequest, @AuthenticationPrincipal SohoUserDetails sohoUserDetails)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<WalletTransfer> lqw = new LambdaQueryWrapper<WalletTransfer>();
        lqw.eq(walletTransfer.getId() != null, WalletTransfer::getId ,walletTransfer.getId());
        lqw.like(StringUtils.isNotBlank(walletTransfer.getCode()),WalletTransfer::getCode ,walletTransfer.getCode());
        lqw.eq(walletTransfer.getFromUserId() != null, WalletTransfer::getFromUserId ,walletTransfer.getFromUserId());
        lqw.eq(walletTransfer.getFromWalletId() != null, WalletTransfer::getFromWalletId ,walletTransfer.getFromWalletId());
        lqw.eq(walletTransfer.getFromWalletType() != null, WalletTransfer::getFromWalletType ,walletTransfer.getFromWalletType());
        lqw.eq(walletTransfer.getFromAmount() != null, WalletTransfer::getFromAmount ,walletTransfer.getFromAmount());
        lqw.eq(walletTransfer.getToWalletId() != null, WalletTransfer::getToWalletId ,walletTransfer.getToWalletId());
        lqw.eq(walletTransfer.getToWalletType() != null, WalletTransfer::getToWalletType ,walletTransfer.getToWalletType());
        lqw.eq(walletTransfer.getToUserId() != null,WalletTransfer::getToUserId ,walletTransfer.getToUserId());
        lqw.eq(walletTransfer.getToAmount() != null, WalletTransfer::getToAmount ,walletTransfer.getToAmount());
        lqw.like(StringUtils.isNotBlank(walletTransfer.getRemark()),WalletTransfer::getRemark ,walletTransfer.getRemark());
//        lqw.like(StringUtils.isNotBlank(walletTransfer.getUpdatedTime()),WalletTransfer::getUpdatedTime ,walletTransfer.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, WalletTransfer::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, WalletTransfer::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<WalletTransfer> list = walletTransferService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取钱包转账详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "user::walletTransfer::getInfo", name = "获取 钱包转账 详细信息")
    public R<WalletTransfer> getInfo(@PathVariable("id" ) Long id, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        LambdaQueryWrapper<WalletTransfer> lqw = new LambdaQueryWrapper<>();
        lqw.eq(WalletTransfer::getId, id);
        WalletTransfer walletTransfer = walletTransferService.getOne(lqw);
        return R.success(walletTransfer);
    }

    /**
     * 新增钱包转账
     */
    @PostMapping
    @Node(value = "user::walletTransfer::add", name = "新增 钱包转账")
    public R<Boolean> add(@RequestBody WalletTransfer walletTransfer, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        return R.success(walletTransferService.save(walletTransfer));
    }

    /**
     * 修改钱包转账
     */
    @PutMapping
    @Node(value = "user::walletTransfer::edit", name = "修改 钱包转账")
    public R<Boolean> edit(@RequestBody WalletTransfer walletTransfer, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        return R.success(walletTransferService.updateById(walletTransfer));
    }

    /**
     * 删除钱包转账
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "user::walletTransfer::remove", name = "删除 钱包转账")
    public R<Boolean> remove(@PathVariable Long[] ids, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        LambdaQueryWrapper<WalletTransfer> lqw = new LambdaQueryWrapper<>();
        return R.success(walletTransferService.remove(lqw));
    }
}