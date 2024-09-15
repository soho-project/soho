package work.soho.pay.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.annotation.Node;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.pay.biz.domain.PayOrder;
import work.soho.pay.biz.service.PayOrderService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 支付单Controller
 *
 * @author i
 * @date 2022-11-08 13:42:16
 */
@Api(tags = "支付单")
@RequiredArgsConstructor
@RestController
@RequestMapping("/pay/admin/payOrder" )
public class PayOrderController {

    private final PayOrderService payOrderService;

    /**
     * 查询支付单列表
     */
    @GetMapping("/list")
    @Node(value = "payOrder::list", name = "支付单列表")
    public R<PageSerializable<PayOrder>> list(PayOrder payOrder)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<PayOrder> lqw = new LambdaQueryWrapper<PayOrder>();
        if (payOrder.getId() != null){
            lqw.eq(PayOrder::getId ,payOrder.getId());
        }
        if (StringUtils.isNotBlank(payOrder.getOrderNo())){
            lqw.like(PayOrder::getOrderNo ,payOrder.getOrderNo());
        }
        if (StringUtils.isNotBlank(payOrder.getTrackingNo())){
            lqw.like(PayOrder::getTrackingNo ,payOrder.getTrackingNo());
        }
        if (payOrder.getAmount() != null){
            lqw.eq(PayOrder::getAmount ,payOrder.getAmount());
        }
        if (payOrder.getStatus() != null){
            lqw.eq(PayOrder::getStatus ,payOrder.getStatus());
        }
        if (payOrder.getPayedTime() != null){
            lqw.eq(PayOrder::getPayedTime ,payOrder.getPayedTime());
        }
        if (payOrder.getCreatedTime() != null){
            lqw.eq(PayOrder::getCreatedTime ,payOrder.getCreatedTime());
        }
        if (payOrder.getUpdatedTime() != null){
            lqw.eq(PayOrder::getUpdatedTime ,payOrder.getUpdatedTime());
        }
        lqw.orderByDesc(PayOrder::getId);
        List<PayOrder> list = payOrderService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取支付单详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "payOrder::getInfo", name = "支付单详细信息")
    public R<PayOrder> getInfo(@PathVariable("id" ) Long id) {
        return R.success(payOrderService.getById(id));
    }

    /**
     * 新增支付单
     */
    @PostMapping
    @Node(value = "payOrder::add", name = "支付单新增")
    public R<Boolean> add(@RequestBody PayOrder payOrder) {
       payOrder.setCreatedTime(LocalDateTime.now());
       payOrder.setUpdatedTime(LocalDateTime.now());
        return R.success(payOrderService.save(payOrder));
    }

    /**
     * 修改支付单
     */
    @PutMapping
    @Node(value = "payOrder::edit", name = "支付单修改")
    public R<Boolean> edit(@RequestBody PayOrder payOrder) {
       payOrder.setUpdatedTime(LocalDateTime.now());
        return R.success(payOrderService.updateById(payOrder));
    }

    /**
     * 删除支付单
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "payOrder::remove", name = "支付单删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(payOrderService.removeByIds(Arrays.asList(ids)));
    }
}
