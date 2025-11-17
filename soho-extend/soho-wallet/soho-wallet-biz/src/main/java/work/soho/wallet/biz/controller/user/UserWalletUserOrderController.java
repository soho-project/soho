package work.soho.wallet.biz.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.wallet.api.enums.WalletLogEnums;
import work.soho.wallet.api.event.WalletUserOrderEvent;
import work.soho.wallet.api.request.PayRequest;
import work.soho.wallet.api.service.WalletInfoApiService;
import work.soho.wallet.biz.domain.WalletUserOrder;
import work.soho.wallet.biz.enums.WalletUserOrderEnums;
import work.soho.wallet.biz.service.WalletUserOrderService;

import java.util.List;

/**
 * 用户支付单Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet/user/walletUserOrder" )
public class UserWalletUserOrderController {

    private final WalletUserOrderService walletUserOrderService;

    private final WalletInfoApiService walletInfoApiService;

    private final ApplicationContext applicationContext;

    /**
     * 查询用户支付单列表
     */
    @GetMapping("/list")
    @Node(value = "user::walletUserOrder::list", name = "获取 用户支付单 列表")
    public R<PageSerializable<WalletUserOrder>> list(WalletUserOrder walletUserOrder, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<WalletUserOrder> lqw = new LambdaQueryWrapper<WalletUserOrder>();
        lqw.eq(walletUserOrder.getId() != null, WalletUserOrder::getId ,walletUserOrder.getId());
        lqw.eq(walletUserOrder.getWalletId() != null, WalletUserOrder::getWalletId ,walletUserOrder.getWalletId());
        lqw.like(StringUtils.isNotBlank(walletUserOrder.getNo()),WalletUserOrder::getNo ,walletUserOrder.getNo());
//        lqw.like(StringUtils.isNotBlank(walletUserOrder.getAmount()),WalletUserOrder::getAmount ,walletUserOrder.getAmount());
        lqw.like(StringUtils.isNotBlank(walletUserOrder.getOutTrackingNumber()),WalletUserOrder::getOutTrackingNumber ,walletUserOrder.getOutTrackingNumber());
        lqw.eq(walletUserOrder.getStatus() != null, WalletUserOrder::getStatus ,walletUserOrder.getStatus());
        lqw.eq(walletUserOrder.getUpdatedTime() != null, WalletUserOrder::getUpdatedTime ,walletUserOrder.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, WalletUserOrder::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, WalletUserOrder::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<WalletUserOrder> list = walletUserOrderService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }
    /**
     * 修改用户支付单
     */
    @PutMapping
    @Node(value = "user::walletUserOrder::edit", name = "修改 用户支付单")
    public R<Boolean> edit(@RequestBody WalletUserOrder walletUserOrder) {
        return R.success(walletUserOrderService.updateById(walletUserOrder));
    }

    /**
     * 获取用户支付单详细信息
     */
    @GetMapping("/getByNo/{no}")
    public R<WalletUserOrder> getByNo(@PathVariable String no) {
        LambdaQueryWrapper<WalletUserOrder> lqw = new LambdaQueryWrapper<WalletUserOrder>();
        lqw.eq(WalletUserOrder::getNo, no);
        lqw.eq(WalletUserOrder::getStatus, WalletUserOrderEnums.Status.PENDING_PAYMENT.getId());
        return R.success(walletUserOrderService.getOne(lqw));
    }

    /**
     * 支付支付单
     */
    @PostMapping("/pay")
    public R<Long> pay(@AuthenticationPrincipal SohoUserDetails userDetails, @RequestBody PayRequest payRequest) {
        WalletUserOrder walletUserOrder = walletUserOrderService.getByNo(payRequest.getPayCode());
        Long logId = walletInfoApiService.changeWalletAmount(userDetails.getId(), walletUserOrder.getWalletType(),
                WalletLogEnums.BizId.PAY_ORDER.getId(), walletUserOrder.getNo(), walletUserOrder.getAmount().negate(), walletUserOrder.getNotes());

        if(logId != null) {
            // 修改支付单状态
            walletUserOrder.setStatus(WalletUserOrderEnums.Status.PAYMENT_SUCCESSFUL.getId());
            walletUserOrderService.updateById(walletUserOrder);

            // 发布支付成功事件
            WalletUserOrderEvent walletUserOrderEvent = BeanUtils.copy(walletUserOrder, WalletUserOrderEvent.class);
            applicationContext.publishEvent(walletUserOrderEvent);
        }

        return R.success(logId);
    }
}