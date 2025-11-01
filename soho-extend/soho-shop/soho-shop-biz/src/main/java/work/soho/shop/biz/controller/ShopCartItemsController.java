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
import work.soho.shop.biz.domain.ShopCartItems;
import work.soho.shop.biz.service.ShopCartItemsService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 购物车表Controller
 *
 * @author fang
 */
@Api(tags = "购物车表")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/admin/shopCartItems" )
public class ShopCartItemsController {

    private final ShopCartItemsService shopCartItemsService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询购物车表列表
     */
    @GetMapping("/list")
    @Node(value = "shopCartItems::list", name = "获取 购物车表 列表")
    public R<PageSerializable<ShopCartItems>> list(ShopCartItems shopCartItems, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopCartItems> lqw = new LambdaQueryWrapper<>();
        lqw.eq(shopCartItems.getId() != null, ShopCartItems::getId ,shopCartItems.getId());
        lqw.eq(shopCartItems.getUserId() != null, ShopCartItems::getUserId ,shopCartItems.getUserId());
        lqw.like(StringUtils.isNotBlank(shopCartItems.getSessionId()),ShopCartItems::getSessionId ,shopCartItems.getSessionId());
        lqw.eq(shopCartItems.getProductId() != null, ShopCartItems::getProductId ,shopCartItems.getProductId());
        lqw.eq(shopCartItems.getSkuId() != null, ShopCartItems::getSkuId ,shopCartItems.getSkuId());
        lqw.eq(shopCartItems.getQty() != null, ShopCartItems::getQty ,shopCartItems.getQty());
        lqw.eq(shopCartItems.getIsSelected() != null, ShopCartItems::getIsSelected ,shopCartItems.getIsSelected());
        lqw.eq(shopCartItems.getPrice() != null, ShopCartItems::getPrice ,shopCartItems.getPrice());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopCartItems::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopCartItems::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(shopCartItems.getUpdatedTime() != null, ShopCartItems::getUpdatedTime ,shopCartItems.getUpdatedTime());
        lqw.orderByDesc(ShopCartItems::getId);
        List<ShopCartItems> list = shopCartItemsService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取购物车表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "shopCartItems::getInfo", name = "获取 购物车表 详细信息")
    public R<ShopCartItems> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopCartItemsService.getById(id));
    }

    /**
     * 新增购物车表
     */
    @PostMapping
    @Node(value = "shopCartItems::add", name = "新增 购物车表")
    public R<Boolean> add(@RequestBody ShopCartItems shopCartItems) {
        return R.success(shopCartItemsService.save(shopCartItems));
    }

    /**
     * 修改购物车表
     */
    @PutMapping
    @Node(value = "shopCartItems::edit", name = "修改 购物车表")
    public R<Boolean> edit(@RequestBody ShopCartItems shopCartItems) {
        return R.success(shopCartItemsService.updateById(shopCartItems));
    }

    /**
     * 删除购物车表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "shopCartItems::remove", name = "删除 购物车表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopCartItemsService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 购物车表 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ShopCartItems.class)
    @Node(value = "shopCartItems::exportExcel", name = "导出 购物车表 Excel")
    public Object exportExcel(ShopCartItems shopCartItems, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ShopCartItems> lqw = new LambdaQueryWrapper<ShopCartItems>();
        lqw.eq(shopCartItems.getId() != null, ShopCartItems::getId ,shopCartItems.getId());
        lqw.eq(shopCartItems.getUserId() != null, ShopCartItems::getUserId ,shopCartItems.getUserId());
        lqw.like(StringUtils.isNotBlank(shopCartItems.getSessionId()),ShopCartItems::getSessionId ,shopCartItems.getSessionId());
        lqw.eq(shopCartItems.getProductId() != null, ShopCartItems::getProductId ,shopCartItems.getProductId());
        lqw.eq(shopCartItems.getSkuId() != null, ShopCartItems::getSkuId ,shopCartItems.getSkuId());
        lqw.eq(shopCartItems.getQty() != null, ShopCartItems::getQty ,shopCartItems.getQty());
        lqw.eq(shopCartItems.getIsSelected() != null, ShopCartItems::getIsSelected ,shopCartItems.getIsSelected());
        lqw.eq(shopCartItems.getPrice() != null, ShopCartItems::getPrice ,shopCartItems.getPrice());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopCartItems::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopCartItems::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(shopCartItems.getUpdatedTime() != null, ShopCartItems::getUpdatedTime ,shopCartItems.getUpdatedTime());
        lqw.orderByDesc(ShopCartItems::getId);
        return shopCartItemsService.list(lqw);
    }

    /**
     * 导入 购物车表 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "shopCartItems::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ShopCartItems.class, new ReadListener<ShopCartItems>() {
                @Override
                public void invoke(ShopCartItems shopCartItems, AnalysisContext analysisContext) {
                    shopCartItemsService.save(shopCartItems);
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