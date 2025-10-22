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
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.service.AdminDictApiService;
import work.soho.admin.api.vo.OptionVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.shop.api.vo.CouponsVo;
import work.soho.shop.biz.annotation.ShopDB;
import work.soho.shop.biz.domain.ShopCouponApplyRanges;
import work.soho.shop.biz.domain.ShopCoupons;
import work.soho.shop.biz.service.ShopCouponApplyRangesService;
import work.soho.shop.biz.service.ShopCouponsService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 优惠券主表Controller
 *
 * @author fang
 */
@Api(tags = "优惠券主表")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/admin/shopCoupons" )
public class ShopCouponsController {

    private final ShopCouponsService shopCouponsService;
    private final ShopCouponApplyRangesService shopCouponApplyRangesService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询优惠券主表列表
     */
    @GetMapping("/list")
    @Node(value = "shopCoupons::list", name = "获取 优惠券主表 列表")
    public R<PageSerializable<ShopCoupons>> list(ShopCoupons shopCoupons, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopCoupons> lqw = new LambdaQueryWrapper<>();
        lqw.eq(shopCoupons.getId() != null, ShopCoupons::getId ,shopCoupons.getId());
        lqw.eq(shopCoupons.getShopId() != null, ShopCoupons::getShopId ,shopCoupons.getShopId());
        lqw.like(StringUtils.isNotBlank(shopCoupons.getName()),ShopCoupons::getName ,shopCoupons.getName());
        lqw.like(StringUtils.isNotBlank(shopCoupons.getCode()),ShopCoupons::getCode ,shopCoupons.getCode());
        lqw.eq(shopCoupons.getType() != null, ShopCoupons::getType ,shopCoupons.getType());
        lqw.like(StringUtils.isNotBlank(shopCoupons.getDescription()),ShopCoupons::getDescription ,shopCoupons.getDescription());
        lqw.eq(shopCoupons.getMinOrderAmount() != null, ShopCoupons::getMinOrderAmount ,shopCoupons.getMinOrderAmount());
        lqw.eq(shopCoupons.getDiscountValue() != null, ShopCoupons::getDiscountValue ,shopCoupons.getDiscountValue());
        lqw.eq(shopCoupons.getMaxDiscountAmount() != null, ShopCoupons::getMaxDiscountAmount ,shopCoupons.getMaxDiscountAmount());
        lqw.eq(shopCoupons.getTotalQuantity() != null, ShopCoupons::getTotalQuantity ,shopCoupons.getTotalQuantity());
        lqw.eq(shopCoupons.getUsedQuantity() != null, ShopCoupons::getUsedQuantity ,shopCoupons.getUsedQuantity());
        lqw.eq(shopCoupons.getLimitPerUser() != null, ShopCoupons::getLimitPerUser ,shopCoupons.getLimitPerUser());
        lqw.eq(shopCoupons.getValidFrom() != null, ShopCoupons::getValidFrom ,shopCoupons.getValidFrom());
        lqw.eq(shopCoupons.getValidTo() != null, ShopCoupons::getValidTo ,shopCoupons.getValidTo());
        lqw.eq(shopCoupons.getStatus() != null, ShopCoupons::getStatus ,shopCoupons.getStatus());
        lqw.eq(shopCoupons.getApplyScope() != null, ShopCoupons::getApplyScope ,shopCoupons.getApplyScope());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopCoupons::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopCoupons::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(shopCoupons.getUpdatedTime() != null, ShopCoupons::getUpdatedTime ,shopCoupons.getUpdatedTime());
        lqw.orderByDesc(ShopCoupons::getId);
        List<ShopCoupons> list = shopCouponsService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取优惠券主表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "shopCoupons::getInfo", name = "获取 优惠券主表 详细信息")
    public R<ShopCoupons> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopCouponsService.getById(id));
    }

    /**
     * 获取优惠券主表详细信息
     */
    @GetMapping(value = "/details/{id}" )
    @Node(value = "shopCoupons::getDetailsInfo", name = "获取 优惠券主表 详细信息")
    public R<CouponsVo> getDetailsInfo(@PathVariable("id" ) Long id) {
        ShopCoupons shopCoupons = shopCouponsService.getById(id);
        CouponsVo couponsVo = BeanUtils.copy(shopCoupons, CouponsVo.class);

        // 查询匹配规则
        List<CouponsVo.CouponApplyScope> couponApplyScopes = shopCouponApplyRangesService.list(new LambdaQueryWrapper<ShopCouponApplyRanges>()
                .eq(ShopCouponApplyRanges::getCouponId, id)
        ).stream().map(item -> {
            return BeanUtils.copy(item, CouponsVo.CouponApplyScope.class);
        }).collect(Collectors.toList());

        couponsVo.setRanges(couponApplyScopes);

        return R.success(couponsVo);
    }

    /**
     * 保存优惠券主表
     */
    @ShopDB
    @PostMapping("/save")
    @Node(value = "shopCoupons::save", name = "保存 优惠券主表")
    public R<Boolean> save(@RequestBody CouponsVo couponsVo) {
        ShopCoupons shopCoupons = BeanUtils.copy(couponsVo, ShopCoupons.class);
        if(StringUtils.isBlank(shopCoupons.getCode())) {
            shopCoupons.setCode(IDGeneratorUtils.snowflake().toString());
        }
        shopCouponsService.saveOrUpdate(shopCoupons);

        // 删除原有规则
        if(couponsVo.getId() != null) {
            shopCouponApplyRangesService.remove(new LambdaQueryWrapper<ShopCouponApplyRanges>().eq(ShopCouponApplyRanges::getCouponId, couponsVo.getId()));
        }

        couponsVo.getRanges().forEach(range->{
            ShopCouponApplyRanges shopCouponsApplyScope = BeanUtils.copy(range, ShopCouponApplyRanges.class);
            shopCouponsApplyScope.setCouponId(shopCoupons.getId());
            shopCouponsApplyScope.setShopId(shopCoupons.getShopId());
            shopCouponApplyRangesService.save(shopCouponsApplyScope);
        });

        return R.success();
    }

    /**
     * 新增优惠券主表
     */
    @PostMapping
    @Node(value = "shopCoupons::add", name = "新增 优惠券主表")
    public R<Boolean> add(@RequestBody ShopCoupons shopCoupons) {
        return R.success(shopCouponsService.save(shopCoupons));
    }

    /**
     * 修改优惠券主表
     */
    @PutMapping
    @Node(value = "shopCoupons::edit", name = "修改 优惠券主表")
    public R<Boolean> edit(@RequestBody ShopCoupons shopCoupons) {
        return R.success(shopCouponsService.updateById(shopCoupons));
    }

    /**
     * 删除优惠券主表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "shopCoupons::remove", name = "删除 优惠券主表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopCouponsService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该优惠券主表 选项
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "shopCoupons::options", name = "获取 优惠券主表 选项")
    public R<List<OptionVo<Long, String>>> options() {
        List<ShopCoupons> list = shopCouponsService.list();
        List<OptionVo<Long, String>> options = new ArrayList<>();

        for(ShopCoupons item: list) {
            OptionVo<Long, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getId());
            optionVo.setLabel(item.getName());
            options.add(optionVo);
        }
        return R.success(options);
    }

    /**
     * 导出 优惠券主表 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ShopCoupons.class)
    @Node(value = "shopCoupons::exportExcel", name = "导出 优惠券主表 Excel")
    public Object exportExcel(ShopCoupons shopCoupons, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ShopCoupons> lqw = new LambdaQueryWrapper<ShopCoupons>();
        lqw.eq(shopCoupons.getId() != null, ShopCoupons::getId ,shopCoupons.getId());
        lqw.eq(shopCoupons.getShopId() != null, ShopCoupons::getShopId ,shopCoupons.getShopId());
        lqw.like(StringUtils.isNotBlank(shopCoupons.getName()),ShopCoupons::getName ,shopCoupons.getName());
        lqw.like(StringUtils.isNotBlank(shopCoupons.getCode()),ShopCoupons::getCode ,shopCoupons.getCode());
        lqw.eq(shopCoupons.getType() != null, ShopCoupons::getType ,shopCoupons.getType());
        lqw.like(StringUtils.isNotBlank(shopCoupons.getDescription()),ShopCoupons::getDescription ,shopCoupons.getDescription());
        lqw.eq(shopCoupons.getMinOrderAmount() != null, ShopCoupons::getMinOrderAmount ,shopCoupons.getMinOrderAmount());
        lqw.eq(shopCoupons.getDiscountValue() != null, ShopCoupons::getDiscountValue ,shopCoupons.getDiscountValue());
        lqw.eq(shopCoupons.getMaxDiscountAmount() != null, ShopCoupons::getMaxDiscountAmount ,shopCoupons.getMaxDiscountAmount());
        lqw.eq(shopCoupons.getTotalQuantity() != null, ShopCoupons::getTotalQuantity ,shopCoupons.getTotalQuantity());
        lqw.eq(shopCoupons.getUsedQuantity() != null, ShopCoupons::getUsedQuantity ,shopCoupons.getUsedQuantity());
        lqw.eq(shopCoupons.getLimitPerUser() != null, ShopCoupons::getLimitPerUser ,shopCoupons.getLimitPerUser());
        lqw.eq(shopCoupons.getValidFrom() != null, ShopCoupons::getValidFrom ,shopCoupons.getValidFrom());
        lqw.eq(shopCoupons.getValidTo() != null, ShopCoupons::getValidTo ,shopCoupons.getValidTo());
        lqw.eq(shopCoupons.getStatus() != null, ShopCoupons::getStatus ,shopCoupons.getStatus());
        lqw.eq(shopCoupons.getApplyScope() != null, ShopCoupons::getApplyScope ,shopCoupons.getApplyScope());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopCoupons::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopCoupons::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(shopCoupons.getUpdatedTime() != null, ShopCoupons::getUpdatedTime ,shopCoupons.getUpdatedTime());
        lqw.orderByDesc(ShopCoupons::getId);
        return shopCouponsService.list(lqw);
    }

    /**
     * 导入 优惠券主表 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "shopCoupons::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ShopCoupons.class, new ReadListener<ShopCoupons>() {
                @Override
                public void invoke(ShopCoupons shopCoupons, AnalysisContext analysisContext) {
                    shopCouponsService.save(shopCoupons);
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