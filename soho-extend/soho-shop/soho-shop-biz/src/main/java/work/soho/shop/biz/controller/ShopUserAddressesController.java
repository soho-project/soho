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
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.shop.biz.domain.ShopUserAddresses;
import work.soho.shop.biz.service.ShopUserAddressesService;

import java.util.Arrays;
import java.util.List;
/**
 * 用户收货地址表Controller
 *
 * @author fang
 */
@Api(tags = "用户收货地址表")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/admin/shopUserAddresses" )
public class ShopUserAddressesController {

    private final ShopUserAddressesService shopUserAddressesService;

    /**
     * 查询用户收货地址表列表
     */
    @GetMapping("/list")
    @Node(value = "shopUserAddresses::list", name = "获取 用户收货地址表 列表")
    public R<PageSerializable<ShopUserAddresses>> list(ShopUserAddresses shopUserAddresses, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopUserAddresses> lqw = new LambdaQueryWrapper<>();
        lqw.eq(shopUserAddresses.getId() != null, ShopUserAddresses::getId ,shopUserAddresses.getId());
        lqw.eq(shopUserAddresses.getUserId() != null, ShopUserAddresses::getUserId ,shopUserAddresses.getUserId());
        lqw.like(StringUtils.isNotBlank(shopUserAddresses.getRecipientName()),ShopUserAddresses::getRecipientName ,shopUserAddresses.getRecipientName());
        lqw.like(StringUtils.isNotBlank(shopUserAddresses.getRecipientPhone()),ShopUserAddresses::getRecipientPhone ,shopUserAddresses.getRecipientPhone());
        lqw.like(StringUtils.isNotBlank(shopUserAddresses.getCountry()),ShopUserAddresses::getCountry ,shopUserAddresses.getCountry());
        lqw.like(StringUtils.isNotBlank(shopUserAddresses.getProvince()),ShopUserAddresses::getProvince ,shopUserAddresses.getProvince());
        lqw.like(StringUtils.isNotBlank(shopUserAddresses.getCity()),ShopUserAddresses::getCity ,shopUserAddresses.getCity());
        lqw.like(StringUtils.isNotBlank(shopUserAddresses.getDistrict()),ShopUserAddresses::getDistrict ,shopUserAddresses.getDistrict());
        lqw.like(StringUtils.isNotBlank(shopUserAddresses.getPostalCode()),ShopUserAddresses::getPostalCode ,shopUserAddresses.getPostalCode());
        lqw.like(StringUtils.isNotBlank(shopUserAddresses.getDetailAddress()),ShopUserAddresses::getDetailAddress ,shopUserAddresses.getDetailAddress());
        lqw.eq(shopUserAddresses.getIsDefault() != null, ShopUserAddresses::getIsDefault ,shopUserAddresses.getIsDefault());
        lqw.like(StringUtils.isNotBlank(shopUserAddresses.getAddressTags()),ShopUserAddresses::getAddressTags ,shopUserAddresses.getAddressTags());
        lqw.eq(shopUserAddresses.getLatitude() != null, ShopUserAddresses::getLatitude ,shopUserAddresses.getLatitude());
        lqw.eq(shopUserAddresses.getLongitude() != null, ShopUserAddresses::getLongitude ,shopUserAddresses.getLongitude());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopUserAddresses::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopUserAddresses::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(shopUserAddresses.getUpdatedTime() != null, ShopUserAddresses::getUpdatedTime ,shopUserAddresses.getUpdatedTime());
        lqw.eq(shopUserAddresses.getIsDeleted() != null, ShopUserAddresses::getIsDeleted ,shopUserAddresses.getIsDeleted());
        lqw.orderByDesc(ShopUserAddresses::getId);
        List<ShopUserAddresses> list = shopUserAddressesService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取用户收货地址表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "shopUserAddresses::getInfo", name = "获取 用户收货地址表 详细信息")
    public R<ShopUserAddresses> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopUserAddressesService.getById(id));
    }

    /**
     * 新增用户收货地址表
     */
    @PostMapping
    @Node(value = "shopUserAddresses::add", name = "新增 用户收货地址表")
    public R<Boolean> add(@RequestBody ShopUserAddresses shopUserAddresses) {
        return R.success(shopUserAddressesService.save(shopUserAddresses));
    }

    /**
     * 修改用户收货地址表
     */
    @PutMapping
    @Node(value = "shopUserAddresses::edit", name = "修改 用户收货地址表")
    public R<Boolean> edit(@RequestBody ShopUserAddresses shopUserAddresses) {
        return R.success(shopUserAddressesService.updateById(shopUserAddresses));
    }

    /**
     * 删除用户收货地址表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "shopUserAddresses::remove", name = "删除 用户收货地址表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopUserAddressesService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 用户收货地址表 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ShopUserAddresses.class)
    @Node(value = "shopUserAddresses::exportExcel", name = "导出 用户收货地址表 Excel")
    public Object exportExcel(ShopUserAddresses shopUserAddresses, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ShopUserAddresses> lqw = new LambdaQueryWrapper<ShopUserAddresses>();
        lqw.eq(shopUserAddresses.getId() != null, ShopUserAddresses::getId ,shopUserAddresses.getId());
        lqw.eq(shopUserAddresses.getUserId() != null, ShopUserAddresses::getUserId ,shopUserAddresses.getUserId());
        lqw.like(StringUtils.isNotBlank(shopUserAddresses.getRecipientName()),ShopUserAddresses::getRecipientName ,shopUserAddresses.getRecipientName());
        lqw.like(StringUtils.isNotBlank(shopUserAddresses.getRecipientPhone()),ShopUserAddresses::getRecipientPhone ,shopUserAddresses.getRecipientPhone());
        lqw.like(StringUtils.isNotBlank(shopUserAddresses.getCountry()),ShopUserAddresses::getCountry ,shopUserAddresses.getCountry());
        lqw.like(StringUtils.isNotBlank(shopUserAddresses.getProvince()),ShopUserAddresses::getProvince ,shopUserAddresses.getProvince());
        lqw.like(StringUtils.isNotBlank(shopUserAddresses.getCity()),ShopUserAddresses::getCity ,shopUserAddresses.getCity());
        lqw.like(StringUtils.isNotBlank(shopUserAddresses.getDistrict()),ShopUserAddresses::getDistrict ,shopUserAddresses.getDistrict());
        lqw.like(StringUtils.isNotBlank(shopUserAddresses.getPostalCode()),ShopUserAddresses::getPostalCode ,shopUserAddresses.getPostalCode());
        lqw.like(StringUtils.isNotBlank(shopUserAddresses.getDetailAddress()),ShopUserAddresses::getDetailAddress ,shopUserAddresses.getDetailAddress());
        lqw.eq(shopUserAddresses.getIsDefault() != null, ShopUserAddresses::getIsDefault ,shopUserAddresses.getIsDefault());
        lqw.like(StringUtils.isNotBlank(shopUserAddresses.getAddressTags()),ShopUserAddresses::getAddressTags ,shopUserAddresses.getAddressTags());
        lqw.eq(shopUserAddresses.getLatitude() != null, ShopUserAddresses::getLatitude ,shopUserAddresses.getLatitude());
        lqw.eq(shopUserAddresses.getLongitude() != null, ShopUserAddresses::getLongitude ,shopUserAddresses.getLongitude());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopUserAddresses::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopUserAddresses::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(shopUserAddresses.getUpdatedTime() != null, ShopUserAddresses::getUpdatedTime ,shopUserAddresses.getUpdatedTime());
        lqw.eq(shopUserAddresses.getIsDeleted() != null, ShopUserAddresses::getIsDeleted ,shopUserAddresses.getIsDeleted());
        lqw.orderByDesc(ShopUserAddresses::getId);
        return shopUserAddressesService.list(lqw);
    }

    /**
     * 导入 用户收货地址表 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "shopUserAddresses::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ShopUserAddresses.class, new ReadListener<ShopUserAddresses>() {
                @Override
                public void invoke(ShopUserAddresses shopUserAddresses, AnalysisContext analysisContext) {
                    shopUserAddressesService.save(shopUserAddresses);
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