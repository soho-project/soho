package work.soho.shop.biz.controller;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.soho.admin.api.service.AdminDictApiService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.shop.biz.domain.ShopUserCoupons;
import work.soho.shop.biz.service.ShopUserCouponsService;

import java.util.Arrays;
import java.util.List;
/**
 * 用户优惠券表Controller
 *
 * @author fang
 */
@Api(tags = "用户优惠券表")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/admin/shopUserCoupons" )
public class ShopUserCouponsController {

    private final ShopUserCouponsService shopUserCouponsService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询用户优惠券表列表
     */
    @GetMapping("/list")
    @Node(value = "shopUserCoupons::list", name = "获取 用户优惠券表 列表")
    public R<PageSerializable<ShopUserCoupons>> list(ShopUserCoupons shopUserCoupons)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopUserCoupons> lqw = new LambdaQueryWrapper<>();
        lqw.eq(shopUserCoupons.getId() != null, ShopUserCoupons::getId ,shopUserCoupons.getId());
        lqw.eq(shopUserCoupons.getUserId() != null, ShopUserCoupons::getUserId ,shopUserCoupons.getUserId());
        lqw.eq(shopUserCoupons.getCouponId() != null, ShopUserCoupons::getCouponId ,shopUserCoupons.getCouponId());
        lqw.like(StringUtils.isNotBlank(shopUserCoupons.getCouponCode()),ShopUserCoupons::getCouponCode ,shopUserCoupons.getCouponCode());
        lqw.eq(shopUserCoupons.getStatus() != null, ShopUserCoupons::getStatus ,shopUserCoupons.getStatus());
        lqw.eq(shopUserCoupons.getUsedTime() != null, ShopUserCoupons::getUsedTime ,shopUserCoupons.getUsedTime());
        lqw.eq(shopUserCoupons.getOrderId() != null, ShopUserCoupons::getOrderId ,shopUserCoupons.getOrderId());
        lqw.eq(shopUserCoupons.getReceivedAt() != null, ShopUserCoupons::getReceivedAt ,shopUserCoupons.getReceivedAt());
        lqw.eq(shopUserCoupons.getExpiredAt() != null, ShopUserCoupons::getExpiredAt ,shopUserCoupons.getExpiredAt());
        lqw.orderByDesc(ShopUserCoupons::getId);
        List<ShopUserCoupons> list = shopUserCouponsService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取用户优惠券表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "shopUserCoupons::getInfo", name = "获取 用户优惠券表 详细信息")
    public R<ShopUserCoupons> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopUserCouponsService.getById(id));
    }

    /**
     * 新增用户优惠券表
     */
    @PostMapping
    @Node(value = "shopUserCoupons::add", name = "新增 用户优惠券表")
    public R<Boolean> add(@RequestBody ShopUserCoupons shopUserCoupons) {
        return R.success(shopUserCouponsService.save(shopUserCoupons));
    }

    /**
     * 修改用户优惠券表
     */
    @PutMapping
    @Node(value = "shopUserCoupons::edit", name = "修改 用户优惠券表")
    public R<Boolean> edit(@RequestBody ShopUserCoupons shopUserCoupons) {
        return R.success(shopUserCouponsService.updateById(shopUserCoupons));
    }

    /**
     * 删除用户优惠券表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "shopUserCoupons::remove", name = "删除 用户优惠券表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopUserCouponsService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 用户优惠券表 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ShopUserCoupons.class)
    @Node(value = "shopUserCoupons::exportExcel", name = "导出 用户优惠券表 Excel")
    public Object exportExcel(ShopUserCoupons shopUserCoupons)
    {
        LambdaQueryWrapper<ShopUserCoupons> lqw = new LambdaQueryWrapper<ShopUserCoupons>();
        lqw.eq(shopUserCoupons.getId() != null, ShopUserCoupons::getId ,shopUserCoupons.getId());
        lqw.eq(shopUserCoupons.getUserId() != null, ShopUserCoupons::getUserId ,shopUserCoupons.getUserId());
        lqw.eq(shopUserCoupons.getCouponId() != null, ShopUserCoupons::getCouponId ,shopUserCoupons.getCouponId());
        lqw.like(StringUtils.isNotBlank(shopUserCoupons.getCouponCode()),ShopUserCoupons::getCouponCode ,shopUserCoupons.getCouponCode());
        lqw.eq(shopUserCoupons.getStatus() != null, ShopUserCoupons::getStatus ,shopUserCoupons.getStatus());
        lqw.eq(shopUserCoupons.getUsedTime() != null, ShopUserCoupons::getUsedTime ,shopUserCoupons.getUsedTime());
        lqw.eq(shopUserCoupons.getOrderId() != null, ShopUserCoupons::getOrderId ,shopUserCoupons.getOrderId());
        lqw.eq(shopUserCoupons.getReceivedAt() != null, ShopUserCoupons::getReceivedAt ,shopUserCoupons.getReceivedAt());
        lqw.eq(shopUserCoupons.getExpiredAt() != null, ShopUserCoupons::getExpiredAt ,shopUserCoupons.getExpiredAt());
        lqw.orderByDesc(ShopUserCoupons::getId);
        return shopUserCouponsService.list(lqw);
    }

    /**
     * 导入 用户优惠券表 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "shopUserCoupons::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ShopUserCoupons.class, new ReadListener<ShopUserCoupons>() {
                @Override
                public void invoke(ShopUserCoupons shopUserCoupons, AnalysisContext analysisContext) {
                    shopUserCouponsService.save(shopUserCoupons);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                    //nothing todo
                }
            }).sheet().doRead();
            return R.success();
        } catch (Exception e) {
            log.error(e.toString());
            return R.error(e.getMessage());
        }
    }
}