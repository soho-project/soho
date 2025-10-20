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
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.shop.biz.domain.ShopCouponUsageLogs;
import work.soho.shop.biz.service.ShopCouponUsageLogsService;

import java.util.Arrays;
import java.util.List;
/**
 * 优惠券使用记录表Controller
 *
 * @author fang
 */
@Api(tags = "优惠券使用记录表")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/admin/shopCouponUsageLogs" )
public class ShopCouponUsageLogsController {

    private final ShopCouponUsageLogsService shopCouponUsageLogsService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询优惠券使用记录表列表
     */
    @GetMapping("/list")
    @Node(value = "shopCouponUsageLogs::list", name = "获取 优惠券使用记录表 列表")
    public R<PageSerializable<ShopCouponUsageLogs>> list(ShopCouponUsageLogs shopCouponUsageLogs)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopCouponUsageLogs> lqw = new LambdaQueryWrapper<>();
        lqw.eq(shopCouponUsageLogs.getId() != null, ShopCouponUsageLogs::getId ,shopCouponUsageLogs.getId());
        lqw.eq(shopCouponUsageLogs.getUserId() != null, ShopCouponUsageLogs::getUserId ,shopCouponUsageLogs.getUserId());
        lqw.eq(shopCouponUsageLogs.getCouponId() != null, ShopCouponUsageLogs::getCouponId ,shopCouponUsageLogs.getCouponId());
        lqw.eq(shopCouponUsageLogs.getOrderId() != null, ShopCouponUsageLogs::getOrderId ,shopCouponUsageLogs.getOrderId());
        lqw.eq(shopCouponUsageLogs.getOrderAmount() != null, ShopCouponUsageLogs::getOrderAmount ,shopCouponUsageLogs.getOrderAmount());
        lqw.eq(shopCouponUsageLogs.getDiscountAmount() != null, ShopCouponUsageLogs::getDiscountAmount ,shopCouponUsageLogs.getDiscountAmount());
        lqw.eq(shopCouponUsageLogs.getUsedAt() != null, ShopCouponUsageLogs::getUsedAt ,shopCouponUsageLogs.getUsedAt());
        lqw.orderByDesc(ShopCouponUsageLogs::getId);
        List<ShopCouponUsageLogs> list = shopCouponUsageLogsService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取优惠券使用记录表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "shopCouponUsageLogs::getInfo", name = "获取 优惠券使用记录表 详细信息")
    public R<ShopCouponUsageLogs> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopCouponUsageLogsService.getById(id));
    }

    /**
     * 新增优惠券使用记录表
     */
    @PostMapping
    @Node(value = "shopCouponUsageLogs::add", name = "新增 优惠券使用记录表")
    public R<Boolean> add(@RequestBody ShopCouponUsageLogs shopCouponUsageLogs) {
        return R.success(shopCouponUsageLogsService.save(shopCouponUsageLogs));
    }

    /**
     * 修改优惠券使用记录表
     */
    @PutMapping
    @Node(value = "shopCouponUsageLogs::edit", name = "修改 优惠券使用记录表")
    public R<Boolean> edit(@RequestBody ShopCouponUsageLogs shopCouponUsageLogs) {
        return R.success(shopCouponUsageLogsService.updateById(shopCouponUsageLogs));
    }

    /**
     * 删除优惠券使用记录表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "shopCouponUsageLogs::remove", name = "删除 优惠券使用记录表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopCouponUsageLogsService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 优惠券使用记录表 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ShopCouponUsageLogs.class)
    @Node(value = "shopCouponUsageLogs::exportExcel", name = "导出 优惠券使用记录表 Excel")
    public Object exportExcel(ShopCouponUsageLogs shopCouponUsageLogs)
    {
        LambdaQueryWrapper<ShopCouponUsageLogs> lqw = new LambdaQueryWrapper<ShopCouponUsageLogs>();
        lqw.eq(shopCouponUsageLogs.getId() != null, ShopCouponUsageLogs::getId ,shopCouponUsageLogs.getId());
        lqw.eq(shopCouponUsageLogs.getUserId() != null, ShopCouponUsageLogs::getUserId ,shopCouponUsageLogs.getUserId());
        lqw.eq(shopCouponUsageLogs.getCouponId() != null, ShopCouponUsageLogs::getCouponId ,shopCouponUsageLogs.getCouponId());
        lqw.eq(shopCouponUsageLogs.getOrderId() != null, ShopCouponUsageLogs::getOrderId ,shopCouponUsageLogs.getOrderId());
        lqw.eq(shopCouponUsageLogs.getOrderAmount() != null, ShopCouponUsageLogs::getOrderAmount ,shopCouponUsageLogs.getOrderAmount());
        lqw.eq(shopCouponUsageLogs.getDiscountAmount() != null, ShopCouponUsageLogs::getDiscountAmount ,shopCouponUsageLogs.getDiscountAmount());
        lqw.eq(shopCouponUsageLogs.getUsedAt() != null, ShopCouponUsageLogs::getUsedAt ,shopCouponUsageLogs.getUsedAt());
        lqw.orderByDesc(ShopCouponUsageLogs::getId);
        return shopCouponUsageLogsService.list(lqw);
    }

    /**
     * 导入 优惠券使用记录表 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "shopCouponUsageLogs::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ShopCouponUsageLogs.class, new ReadListener<ShopCouponUsageLogs>() {
                @Override
                public void invoke(ShopCouponUsageLogs shopCouponUsageLogs, AnalysisContext analysisContext) {
                    shopCouponUsageLogsService.save(shopCouponUsageLogs);
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