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
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.shop.biz.domain.ShopInfo;
import work.soho.shop.biz.service.ShopInfoService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * 店铺信息Controller
 *
 * @author fang
 */
@Api(tags = "店铺信息")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/admin/shopInfo" )
public class ShopInfoController {

    private final ShopInfoService shopInfoService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询店铺信息列表
     */
    @GetMapping("/list")
    @Node(value = "shopInfo::list", name = "获取 店铺信息 列表")
    public R<PageSerializable<ShopInfo>> list(ShopInfo shopInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopInfo> lqw = new LambdaQueryWrapper<>();
        lqw.eq(shopInfo.getId() != null, ShopInfo::getId ,shopInfo.getId());
        lqw.like(StringUtils.isNotBlank(shopInfo.getName()),ShopInfo::getName ,shopInfo.getName());
        lqw.like(StringUtils.isNotBlank(shopInfo.getKeywords()),ShopInfo::getKeywords ,shopInfo.getKeywords());
        lqw.like(StringUtils.isNotBlank(shopInfo.getIntroduction()),ShopInfo::getIntroduction ,shopInfo.getIntroduction());
        lqw.like(StringUtils.isNotBlank(shopInfo.getTel()),ShopInfo::getTel ,shopInfo.getTel());
        lqw.like(StringUtils.isNotBlank(shopInfo.getAddress()),ShopInfo::getAddress ,shopInfo.getAddress());
        lqw.like(StringUtils.isNotBlank(shopInfo.getShopQualificationsImgs()),ShopInfo::getShopQualificationsImgs ,shopInfo.getShopQualificationsImgs());
        lqw.eq(shopInfo.getUserId() != null, ShopInfo::getUserId ,shopInfo.getUserId());
        lqw.eq(shopInfo.getStatus() != null, ShopInfo::getStatus ,shopInfo.getStatus());
        lqw.eq(shopInfo.getUpdatedTime() != null, ShopInfo::getUpdatedTime ,shopInfo.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ShopInfo::getId);
        List<ShopInfo> list = shopInfoService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取店铺信息详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "shopInfo::getInfo", name = "获取 店铺信息 详细信息")
    public R<ShopInfo> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopInfoService.getById(id));
    }

    /**
     * 新增店铺信息
     */
    @PostMapping
    @Node(value = "shopInfo::add", name = "新增 店铺信息")
    public R<Boolean> add(@RequestBody ShopInfo shopInfo) {
        return R.success(shopInfoService.save(shopInfo));
    }

    /**
     * 修改店铺信息
     */
    @PutMapping
    @Node(value = "shopInfo::edit", name = "修改 店铺信息")
    public R<Boolean> edit(@RequestBody ShopInfo shopInfo) {
        return R.success(shopInfoService.updateById(shopInfo));
    }

    /**
     * 删除店铺信息
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "shopInfo::remove", name = "删除 店铺信息")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopInfoService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该店铺信息 选项
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "shopInfo::options", name = "获取 店铺信息 选项")
    public R<List<OptionVo<Integer, String>>> options() {
        List<ShopInfo> list = shopInfoService.list();
        List<OptionVo<Integer, String>> options = new ArrayList<>();

        for(ShopInfo item: list) {
            OptionVo<Integer, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getId());
            optionVo.setLabel(item.getName());
            options.add(optionVo);
        }
        return R.success(options);
    }

    /**
     * 导出 店铺信息 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ShopInfo.class)
    @Node(value = "shopInfo::exportExcel", name = "导出 店铺信息 Excel")
    public Object exportExcel(ShopInfo shopInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ShopInfo> lqw = new LambdaQueryWrapper<ShopInfo>();
        lqw.eq(shopInfo.getId() != null, ShopInfo::getId ,shopInfo.getId());
        lqw.like(StringUtils.isNotBlank(shopInfo.getName()),ShopInfo::getName ,shopInfo.getName());
        lqw.like(StringUtils.isNotBlank(shopInfo.getKeywords()),ShopInfo::getKeywords ,shopInfo.getKeywords());
        lqw.like(StringUtils.isNotBlank(shopInfo.getIntroduction()),ShopInfo::getIntroduction ,shopInfo.getIntroduction());
        lqw.like(StringUtils.isNotBlank(shopInfo.getTel()),ShopInfo::getTel ,shopInfo.getTel());
        lqw.like(StringUtils.isNotBlank(shopInfo.getAddress()),ShopInfo::getAddress ,shopInfo.getAddress());
        lqw.like(StringUtils.isNotBlank(shopInfo.getShopQualificationsImgs()),ShopInfo::getShopQualificationsImgs ,shopInfo.getShopQualificationsImgs());
        lqw.eq(shopInfo.getUserId() != null, ShopInfo::getUserId ,shopInfo.getUserId());
        lqw.eq(shopInfo.getStatus() != null, ShopInfo::getStatus ,shopInfo.getStatus());
        lqw.eq(shopInfo.getUpdatedTime() != null, ShopInfo::getUpdatedTime ,shopInfo.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ShopInfo::getId);
        return shopInfoService.list(lqw);
    }

    /**
     * 导入 店铺信息 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "shopInfo::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ShopInfo.class, new ReadListener<ShopInfo>() {
                @Override
                public void invoke(ShopInfo shopInfo, AnalysisContext analysisContext) {
                    shopInfoService.save(shopInfo);
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