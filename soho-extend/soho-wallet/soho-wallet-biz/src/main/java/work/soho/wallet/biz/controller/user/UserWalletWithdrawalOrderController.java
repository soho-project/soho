package work.soho.wallet.biz.controller.user;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import work.soho.common.core.util.IDGeneratorUtils;
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
import work.soho.common.core.util.StringUtils;
import com.github.pagehelper.PageSerializable;
import work.soho.common.core.result.R;
import work.soho.common.security.annotation.Node;
import work.soho.user.api.dto.UserInfoDto;
import work.soho.wallet.api.request.CreateWithdrawalOrderRequest;
import work.soho.wallet.biz.domain.*;
import work.soho.wallet.biz.enums.WalletWithdrawalOrderEnums;
import work.soho.wallet.biz.service.WalletBankCardService;
import work.soho.wallet.biz.service.WalletInfoService;
import work.soho.wallet.biz.service.WalletTypeService;
import work.soho.wallet.biz.service.WalletWithdrawalOrderService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
import work.soho.user.api.service.UserApiService;

/**
 * 提现单Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet/user/walletWithdrawalOrder" )
public class UserWalletWithdrawalOrderController {

    private final WalletWithdrawalOrderService walletWithdrawalOrderService;
    private final WalletBankCardService walletBankCardService;
    private final WalletInfoService walletInfoService;
    private final UserApiService userApiService;
    private final WalletTypeService walletTypeService;

    /**
     * 查询提现单列表
     */
    @GetMapping("/list")
    @Node(value = "user::walletWithdrawalOrder::list", name = "获取 提现单 列表")
    public R<PageSerializable<WalletWithdrawalOrder>> list(WalletWithdrawalOrder walletWithdrawalOrder, BetweenCreatedTimeRequest betweenCreatedTimeRequest, @AuthenticationPrincipal SohoUserDetails sohoUserDetails)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<WalletWithdrawalOrder> lqw = new LambdaQueryWrapper<WalletWithdrawalOrder>();
        lqw.eq(walletWithdrawalOrder.getId() != null, WalletWithdrawalOrder::getId ,walletWithdrawalOrder.getId());
        lqw.eq(WalletWithdrawalOrder::getUserId ,sohoUserDetails.getId());
        lqw.eq(walletWithdrawalOrder.getWalletId() != null, WalletWithdrawalOrder::getWalletId ,walletWithdrawalOrder.getWalletId());
        lqw.eq(walletWithdrawalOrder.getAmount() != null, WalletWithdrawalOrder::getAmount ,walletWithdrawalOrder.getAmount());
        lqw.like(StringUtils.isNotBlank(walletWithdrawalOrder.getNotes()),WalletWithdrawalOrder::getNotes ,walletWithdrawalOrder.getNotes());
        lqw.eq(walletWithdrawalOrder.getStatus() != null, WalletWithdrawalOrder::getStatus ,walletWithdrawalOrder.getStatus());
        lqw.eq(walletWithdrawalOrder.getAdminId() != null, WalletWithdrawalOrder::getAdminId ,walletWithdrawalOrder.getAdminId());
        lqw.like(StringUtils.isNotBlank(walletWithdrawalOrder.getAdminNotes()),WalletWithdrawalOrder::getAdminNotes ,walletWithdrawalOrder.getAdminNotes());
        lqw.eq(walletWithdrawalOrder.getUpdatedTime() != null, WalletWithdrawalOrder::getUpdatedTime ,walletWithdrawalOrder.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, WalletWithdrawalOrder::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, WalletWithdrawalOrder::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<WalletWithdrawalOrder> list = walletWithdrawalOrderService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取提现单详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "user::walletWithdrawalOrder::getInfo", name = "获取 提现单 详细信息")
    public R<WalletWithdrawalOrder> getInfo(@PathVariable("id" ) Long id, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        LambdaQueryWrapper<WalletWithdrawalOrder> lqw = new LambdaQueryWrapper<>();
        lqw.eq(WalletWithdrawalOrder::getId, id);
        lqw.eq(WalletWithdrawalOrder::getUserId ,sohoUserDetails.getId());
        WalletWithdrawalOrder walletWithdrawalOrder = walletWithdrawalOrderService.getOne(lqw);
        return R.success(walletWithdrawalOrder);
    }

    /**
     * 新增提现单
     */
    @PostMapping
    @Node(value = "user::walletWithdrawalOrder::add", name = "新增 提现单")
    public R<Boolean> add(@RequestBody WalletWithdrawalOrder walletWithdrawalOrder, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
       walletWithdrawalOrder.setUserId(sohoUserDetails.getId());
        return R.success(walletWithdrawalOrderService.save(walletWithdrawalOrder));
    }
    /**
     * 新增提现单
     */
    @PostMapping("/create")
    @Transactional
    public R<WalletLog> create(@RequestBody CreateWithdrawalOrderRequest createWithdrawalOrderRequest, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        // 获取用户信息
        UserInfoDto userInfo = userApiService.getUserById(sohoUserDetails.getId());
        if (userInfo == null || !Objects.equals(userInfo.getId(), sohoUserDetails.getId())) {
            return R.error("钱包信息错误");
        }
        // 验证支付密码
        if (!userApiService.verificationUserInfoPayPassword(userInfo.getId(), createWithdrawalOrderRequest.getPayPassword())) {
            return R.error("支付密码错误");
        }

        // 获取钱包信息
        WalletInfo walletInfo = walletInfoService.getById(createWithdrawalOrderRequest.getWalletId());
        if (walletInfo == null || !Objects.equals(walletInfo.getUserId(), sohoUserDetails.getId())) {
            return R.error("钱包信息错误");
        }

        // 检查是否支持提现
        WalletType walletType = walletTypeService.getById(walletInfo.getType());
        if(walletType == null || walletType.getCanWithdrawal() == 0) {
            return R.error("该钱包不支持提现");
        }

        // TODO 汇率处理

        //获取银行卡信息
        WalletBankCard card = walletBankCardService.getWithUserId(sohoUserDetails.getId(), createWithdrawalOrderRequest.getWithdrawBankId());
        if(card == null) {
            return R.error("银行卡信息错误");
        }

        // 计算手续费
        BigDecimal fee = walletTypeService.getCommission(walletInfo.getType(), createWithdrawalOrderRequest.getAmount());
        BigDecimal payAmount = createWithdrawalOrderRequest.getAmount().subtract(fee);

        WalletWithdrawalOrder walletWithdrawalOrder = new WalletWithdrawalOrder();
        walletWithdrawalOrder.setCode(IDGeneratorUtils.snowflake().toString());
        walletWithdrawalOrder.setUserId(sohoUserDetails.getId());
        walletWithdrawalOrder.setWalletId(createWithdrawalOrderRequest.getWalletId());
        walletWithdrawalOrder.setCardCode(card.getCardCode());
        walletWithdrawalOrder.setCardName(card.getName());
        walletWithdrawalOrder.setCardPhone(card.getPhone());
        walletWithdrawalOrder.setAmount(createWithdrawalOrderRequest.getAmount());
        walletWithdrawalOrder.setCommissionAmount(fee);
        walletWithdrawalOrder.setPayAmount(payAmount);
        walletWithdrawalOrder.setNotes(createWithdrawalOrderRequest.getNotes());
        walletWithdrawalOrder.setStatus(WalletWithdrawalOrderEnums.Status.PENDING_PROCESSING.getId());
        walletWithdrawalOrder.setUpdatedTime(LocalDateTime.now());
        walletWithdrawalOrder.setCreatedTime(LocalDateTime.now());
        walletWithdrawalOrderService.save(walletWithdrawalOrder);
        // 钱包扣款
        WalletLog walletLog = walletInfoService.updateAmount(walletInfo, walletWithdrawalOrder.getAmount().negate(), "提现;单号：" + walletWithdrawalOrder.getId());

        return R.success(walletLog);
    }

    /**
     * 修改提现单
     */
    @PutMapping
    @Node(value = "user::walletWithdrawalOrder::edit", name = "修改 提现单")
    public R<Boolean> edit(@RequestBody WalletWithdrawalOrder walletWithdrawalOrder, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
       walletWithdrawalOrder.setUserId(sohoUserDetails.getId());
        return R.success(walletWithdrawalOrderService.updateById(walletWithdrawalOrder));
    }

    /**
     * 删除提现单
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "user::walletWithdrawalOrder::remove", name = "删除 提现单")
    public R<Boolean> remove(@PathVariable Long[] ids, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        LambdaQueryWrapper<WalletWithdrawalOrder> lqw = new LambdaQueryWrapper<>();
       lqw.eq(WalletWithdrawalOrder::getUserId, sohoUserDetails.getId());
        return R.success(walletWithdrawalOrderService.remove(lqw));
    }
}