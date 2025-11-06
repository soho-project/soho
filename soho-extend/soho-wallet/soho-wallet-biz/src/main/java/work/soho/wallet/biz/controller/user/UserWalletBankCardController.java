package work.soho.wallet.biz.controller.user;

import java.time.LocalDateTime;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import work.soho.wallet.biz.domain.WalletBankCard;
import work.soho.wallet.biz.service.WalletBankCardService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 用户钱包银行卡信息表Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet/user/walletBankCard" )
public class UserWalletBankCardController {

    private final WalletBankCardService walletBankCardService;

    /**
     * 查询用户钱包银行卡信息表列表
     */
    @GetMapping("/list")
    @Node(value = "user::walletBankCard::list", name = "获取 用户钱包银行卡信息表 列表")
    public R<PageSerializable<WalletBankCard>> list(WalletBankCard walletBankCard, BetweenCreatedTimeRequest betweenCreatedTimeRequest, @AuthenticationPrincipal SohoUserDetails sohoUserDetails)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<WalletBankCard> lqw = new LambdaQueryWrapper<WalletBankCard>();
        lqw.eq(walletBankCard.getId() != null, WalletBankCard::getId ,walletBankCard.getId());
        lqw.eq(WalletBankCard::getUserId ,sohoUserDetails.getId());
        lqw.like(StringUtils.isNotBlank(walletBankCard.getName()),WalletBankCard::getName ,walletBankCard.getName());
        lqw.like(StringUtils.isNotBlank(walletBankCard.getIdCode()),WalletBankCard::getIdCode ,walletBankCard.getIdCode());
        lqw.like(StringUtils.isNotBlank(walletBankCard.getCardCode()),WalletBankCard::getCardCode ,walletBankCard.getCardCode());
        lqw.like(StringUtils.isNotBlank(walletBankCard.getPhone()),WalletBankCard::getPhone ,walletBankCard.getPhone());
        lqw.eq(walletBankCard.getUpdatedTime() != null, WalletBankCard::getUpdatedTime ,walletBankCard.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, WalletBankCard::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, WalletBankCard::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<WalletBankCard> list = walletBankCardService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取用户钱包银行卡信息表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "user::walletBankCard::getInfo", name = "获取 用户钱包银行卡信息表 详细信息")
    public R<WalletBankCard> getInfo(@PathVariable("id" ) Long id, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        LambdaQueryWrapper<WalletBankCard> lqw = new LambdaQueryWrapper<>();
        lqw.eq(WalletBankCard::getId, id);
        lqw.eq(WalletBankCard::getUserId ,sohoUserDetails.getId());
        WalletBankCard walletBankCard = walletBankCardService.getOne(lqw);
        return R.success(walletBankCard);
    }

    /**
     * 新增用户钱包银行卡信息表
     */
    @PostMapping
    @Node(value = "user::walletBankCard::add", name = "新增 用户钱包银行卡信息表")
    public R<Boolean> add(@RequestBody WalletBankCard walletBankCard, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
       walletBankCard.setUserId(sohoUserDetails.getId());
        return R.success(walletBankCardService.save(walletBankCard));
    }

    /**
     * 修改用户钱包银行卡信息表
     */
    @PutMapping
    @Node(value = "user::walletBankCard::edit", name = "修改 用户钱包银行卡信息表")
    public R<Boolean> edit(@RequestBody WalletBankCard walletBankCard, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
       walletBankCard.setUserId(sohoUserDetails.getId());
        return R.success(walletBankCardService.updateById(walletBankCard));
    }

    /**
     * 删除用户钱包银行卡信息表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "user::walletBankCard::remove", name = "删除 用户钱包银行卡信息表")
    public R<Boolean> remove(@PathVariable Long[] ids, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        LambdaQueryWrapper<WalletBankCard> lqw = new LambdaQueryWrapper<>();
       lqw.eq(WalletBankCard::getUserId, sohoUserDetails.getId());
        return R.success(walletBankCardService.remove(lqw));
    }
}