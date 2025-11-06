package work.soho.wallet.biz.controller.user;

import java.time.LocalDateTime;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import work.soho.common.security.userdetails.SohoUserDetails;import work.soho.common.core.util.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.security.utils.SecurityUtils;
import work.soho.common.core.util.StringUtils;
import com.github.pagehelper.PageSerializable;
import work.soho.common.core.result.R;
import work.soho.common.security.annotation.Node;
import work.soho.wallet.api.vo.WalletTransferVo;
import work.soho.wallet.biz.domain.WalletTransfer;
import work.soho.wallet.biz.service.WalletTransferService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
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

    /**
     * 茸元转人民币
     *
     * @param sohoUserDetails
     * @param walletTransferVo
     * @return
     */
    @Transactional
    @PostMapping("/ry2RmbTransfer")
    public R<WalletTransfer> ry2RmbTransfer(@AuthenticationPrincipal SohoUserDetails sohoUserDetails, @RequestBody WalletTransferVo walletTransferVo)
    {
        try {
            WalletTransfer walletTransfer = walletTransferService.ry2RmbTransfer(sohoUserDetails.getId(), walletTransferVo.getAmount(), walletTransferVo.getRemark(), walletTransferVo.getPayPassword());
            return R.success(walletTransfer);
        } catch (Exception e) {
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