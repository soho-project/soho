package work.soho.shop.biz.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.shop.api.request.OrderCreateRequest;
import work.soho.shop.biz.domain.ShopOrderInfo;
import work.soho.shop.biz.service.ShopOrderInfoService;

import java.util.Arrays;
import java.util.List;

;
/**
 * 订单Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/user/shopOrderInfo" )
public class UserShopOrderInfoController {

    private final ShopOrderInfoService shopOrderInfoService;

    /**
     * 查询订单列表
     */
    @GetMapping("/list")
    @Node(value = "user::shopOrderInfo::list", name = "获取 订单 列表")
    public R<PageSerializable<ShopOrderInfo>> list(ShopOrderInfo shopOrderInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopOrderInfo> lqw = new LambdaQueryWrapper<ShopOrderInfo>();
        lqw.eq(shopOrderInfo.getId() != null, ShopOrderInfo::getId ,shopOrderInfo.getId());
        lqw.like(StringUtils.isNotBlank(shopOrderInfo.getNo()),ShopOrderInfo::getNo ,shopOrderInfo.getNo());
        lqw.eq(shopOrderInfo.getUserId() != null, ShopOrderInfo::getUserId ,shopOrderInfo.getUserId());
        lqw.eq(shopOrderInfo.getAmount() != null, ShopOrderInfo::getAmount ,shopOrderInfo.getAmount());
        lqw.eq(shopOrderInfo.getStatus() != null, ShopOrderInfo::getStatus ,shopOrderInfo.getStatus());
        lqw.eq(shopOrderInfo.getPayStatus() != null, ShopOrderInfo::getPayStatus ,shopOrderInfo.getPayStatus());
        lqw.eq(shopOrderInfo.getFreightStatus() != null, ShopOrderInfo::getFreightStatus ,shopOrderInfo.getFreightStatus());
        lqw.like(StringUtils.isNotBlank(shopOrderInfo.getReceivingAddress()),ShopOrderInfo::getReceivingAddress ,shopOrderInfo.getReceivingAddress());
        lqw.like(StringUtils.isNotBlank(shopOrderInfo.getConsignee()),ShopOrderInfo::getConsignee ,shopOrderInfo.getConsignee());
        lqw.like(StringUtils.isNotBlank(shopOrderInfo.getReceivingPhoneNumber()),ShopOrderInfo::getReceivingPhoneNumber ,shopOrderInfo.getReceivingPhoneNumber());
        lqw.eq(shopOrderInfo.getUpdatedTime() != null, ShopOrderInfo::getUpdatedTime ,shopOrderInfo.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopOrderInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopOrderInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<ShopOrderInfo> list = shopOrderInfoService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取订单详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "user::shopOrderInfo::getInfo", name = "获取 订单 详细信息")
    public R<ShopOrderInfo> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopOrderInfoService.getById(id));
    }

    /**
     * 新增订单
     *
     * 创建订单
     */
    @PostMapping
    @Node(value = "user::shopOrderInfo::create", name = "新增 订单")
    public R<ShopOrderInfo> create(@RequestBody OrderCreateRequest orderCreateRequest, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        orderCreateRequest.setUserId(sohoUserDetails.getId());
        ShopOrderInfo order = shopOrderInfoService.createOrder(orderCreateRequest);
        return R.success(order);
    }

    /**
     * 修改订单
     */
    @PutMapping
    @Node(value = "user::shopOrderInfo::edit", name = "修改 订单")
    public R<Boolean> edit(@RequestBody ShopOrderInfo shopOrderInfo) {
        return R.success(shopOrderInfoService.updateById(shopOrderInfo));
    }

    /**
     * 删除订单
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "user::shopOrderInfo::remove", name = "删除 订单")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopOrderInfoService.removeByIds(Arrays.asList(ids)));
    }
}