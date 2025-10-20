package work.soho.shop.biz.controller;

import java.time.LocalDateTime;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.annotations.Api;
import work.soho.common.core.util.PageUtils;
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
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.admin.api.service.AdminDictApiService;
import work.soho.shop.biz.domain.ShopCouponApplyRanges;
import work.soho.shop.biz.service.ShopCouponApplyRangesService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 优惠券适用范围表Controller
 *
 * @author fang
 */
@Api(tags = "优惠券适用范围表")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/admin/shopCouponApplyRanges" )
public class ShopCouponApplyRangesController {

    private final ShopCouponApplyRangesService shopCouponApplyRangesService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询优惠券适用范围表列表
     */
    @GetMapping("/list")
    @Node(value = "shopCouponApplyRanges::list", name = "获取 优惠券适用范围表 列表")
    public R<PageSerializable<ShopCouponApplyRanges>> list(ShopCouponApplyRanges shopCouponApplyRanges, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopCouponApplyRanges> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotBlank(shopCouponApplyRanges.getScopeName()),ShopCouponApplyRanges::getScopeName ,shopCouponApplyRanges.getScopeName());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopCouponApplyRanges::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopCouponApplyRanges::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(shopCouponApplyRanges.getId() != null, ShopCouponApplyRanges::getId ,shopCouponApplyRanges.getId());
        lqw.eq(shopCouponApplyRanges.getCouponId() != null, ShopCouponApplyRanges::getCouponId ,shopCouponApplyRanges.getCouponId());
        lqw.eq(shopCouponApplyRanges.getShopId() != null, ShopCouponApplyRanges::getShopId ,shopCouponApplyRanges.getShopId());
        lqw.eq(shopCouponApplyRanges.getScopeType() != null, ShopCouponApplyRanges::getScopeType ,shopCouponApplyRanges.getScopeType());
        lqw.eq(shopCouponApplyRanges.getScopeId() != null, ShopCouponApplyRanges::getScopeId ,shopCouponApplyRanges.getScopeId());
        lqw.orderByDesc(ShopCouponApplyRanges::getId);
        List<ShopCouponApplyRanges> list = shopCouponApplyRangesService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取优惠券适用范围表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "shopCouponApplyRanges::getInfo", name = "获取 优惠券适用范围表 详细信息")
    public R<ShopCouponApplyRanges> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopCouponApplyRangesService.getById(id));
    }

    /**
     * 新增优惠券适用范围表
     */
    @PostMapping
    @Node(value = "shopCouponApplyRanges::add", name = "新增 优惠券适用范围表")
    public R<Boolean> add(@RequestBody ShopCouponApplyRanges shopCouponApplyRanges) {
        return R.success(shopCouponApplyRangesService.save(shopCouponApplyRanges));
    }

    /**
     * 修改优惠券适用范围表
     */
    @PutMapping
    @Node(value = "shopCouponApplyRanges::edit", name = "修改 优惠券适用范围表")
    public R<Boolean> edit(@RequestBody ShopCouponApplyRanges shopCouponApplyRanges) {
        return R.success(shopCouponApplyRangesService.updateById(shopCouponApplyRanges));
    }

    /**
     * 删除优惠券适用范围表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "shopCouponApplyRanges::remove", name = "删除 优惠券适用范围表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopCouponApplyRangesService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 优惠券适用范围表 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ShopCouponApplyRanges.class)
    @Node(value = "shopCouponApplyRanges::exportExcel", name = "导出 优惠券适用范围表 Excel")
    public Object exportExcel(ShopCouponApplyRanges shopCouponApplyRanges, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ShopCouponApplyRanges> lqw = new LambdaQueryWrapper<ShopCouponApplyRanges>();
        lqw.like(StringUtils.isNotBlank(shopCouponApplyRanges.getScopeName()),ShopCouponApplyRanges::getScopeName ,shopCouponApplyRanges.getScopeName());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopCouponApplyRanges::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopCouponApplyRanges::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(shopCouponApplyRanges.getId() != null, ShopCouponApplyRanges::getId ,shopCouponApplyRanges.getId());
        lqw.eq(shopCouponApplyRanges.getCouponId() != null, ShopCouponApplyRanges::getCouponId ,shopCouponApplyRanges.getCouponId());
        lqw.eq(shopCouponApplyRanges.getShopId() != null, ShopCouponApplyRanges::getShopId ,shopCouponApplyRanges.getShopId());
        lqw.eq(shopCouponApplyRanges.getScopeType() != null, ShopCouponApplyRanges::getScopeType ,shopCouponApplyRanges.getScopeType());
        lqw.eq(shopCouponApplyRanges.getScopeId() != null, ShopCouponApplyRanges::getScopeId ,shopCouponApplyRanges.getScopeId());
        lqw.orderByDesc(ShopCouponApplyRanges::getId);
        return shopCouponApplyRangesService.list(lqw);
    }

    /**
     * 导入 优惠券适用范围表 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "shopCouponApplyRanges::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ShopCouponApplyRanges.class, new ReadListener<ShopCouponApplyRanges>() {
                @Override
                public void invoke(ShopCouponApplyRanges shopCouponApplyRanges, AnalysisContext analysisContext) {
                    shopCouponApplyRangesService.save(shopCouponApplyRanges);
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