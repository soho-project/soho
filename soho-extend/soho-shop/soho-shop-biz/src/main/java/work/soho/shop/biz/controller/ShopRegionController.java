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
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.shop.biz.domain.ShopRegion;
import work.soho.shop.biz.service.ShopRegionService;

import java.util.*;
import java.util.stream.Collectors;
/**
 * 地区信息表Controller
 *
 * @author fang
 */
@Api(tags = "地区信息表")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/admin/shopRegion" )
public class ShopRegionController {

    private final ShopRegionService shopRegionService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询地区信息表列表
     */
    @GetMapping("/list")
    @Node(value = "shopRegion::list", name = "获取 地区信息表 列表")
    public R<PageSerializable<ShopRegion>> list(ShopRegion shopRegion, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopRegion> lqw = new LambdaQueryWrapper<>();
        lqw.eq(shopRegion.getId() != null, ShopRegion::getId ,shopRegion.getId());
        lqw.like(StringUtils.isNotBlank(shopRegion.getRegionCode()),ShopRegion::getRegionCode ,shopRegion.getRegionCode());
        lqw.like(StringUtils.isNotBlank(shopRegion.getRegionName()),ShopRegion::getRegionName ,shopRegion.getRegionName());
        lqw.like(StringUtils.isNotBlank(shopRegion.getParentCode()),ShopRegion::getParentCode ,shopRegion.getParentCode());
        lqw.eq(shopRegion.getRegionLevel() != null, ShopRegion::getRegionLevel ,shopRegion.getRegionLevel());
        lqw.eq(shopRegion.getIsRemote() != null, ShopRegion::getIsRemote ,shopRegion.getIsRemote());
        lqw.eq(shopRegion.getSortOrder() != null, ShopRegion::getSortOrder ,shopRegion.getSortOrder());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopRegion::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopRegion::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ShopRegion::getId);
        List<ShopRegion> list = shopRegionService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取地区信息表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "shopRegion::getInfo", name = "获取 地区信息表 详细信息")
    public R<ShopRegion> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopRegionService.getById(id));
    }

    /**
     * 新增地区信息表
     */
    @PostMapping
    @Node(value = "shopRegion::add", name = "新增 地区信息表")
    public R<Boolean> add(@RequestBody ShopRegion shopRegion) {
        return R.success(shopRegionService.save(shopRegion));
    }

    /**
     * 修改地区信息表
     */
    @PutMapping
    @Node(value = "shopRegion::edit", name = "修改 地区信息表")
    public R<Boolean> edit(@RequestBody ShopRegion shopRegion) {
        return R.success(shopRegionService.updateById(shopRegion));
    }

    /**
     * 删除地区信息表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "shopRegion::remove", name = "删除 地区信息表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        List<ShopRegion> oldData = shopRegionService.listByIds(Arrays.asList(ids));
        if(oldData.size() != ids.length) {
            return R.error("数据不存在");
        }
        //检查是否有子节点
        LambdaQueryWrapper<ShopRegion> lqw = new LambdaQueryWrapper<>();
        lqw.in(ShopRegion::getParentCode, Arrays.asList(ids));
        lqw.notIn(ShopRegion::getId, Arrays.asList(ids));
        List<ShopRegion> list = shopRegionService.list(lqw);
        if (!list.isEmpty()) {
            return R.error("还有子节点，无法删除");
        }
        return R.success(shopRegionService.removeByIds(Arrays.asList(ids)));
    }

    @GetMapping("tree")
    public R<List<TreeNodeVo>> tree() {
        List<ShopRegion> list = shopRegionService.list();
        List<TreeNodeVo<String, String, String, String>> listVo = list.stream().map(item->{
            return new TreeNodeVo<>(item.getRegionCode(), item.getRegionCode(), item.getParentCode(), item.getRegionName());
        }).collect(Collectors.toList());

        Map<String, List<TreeNodeVo>> mapVo = new HashMap<>();
        listVo.stream().forEach(item -> {
            if(mapVo.get(item.getParentId()) == null) {
                mapVo.put(item.getParentId(), new ArrayList<>());
            }
            mapVo.get(item.getParentId()).add(item);
        });

        listVo.forEach(item -> {
            if(mapVo.containsKey(item.getKey())) {
                item.setChildren(mapVo.get(item.getKey()));
            }
        });
        return R.success(mapVo.get("0"));
    }

    /**
     * 导出 地区信息表 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ShopRegion.class)
    @Node(value = "shopRegion::exportExcel", name = "导出 地区信息表 Excel")
    public Object exportExcel(ShopRegion shopRegion, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ShopRegion> lqw = new LambdaQueryWrapper<ShopRegion>();
        lqw.eq(shopRegion.getId() != null, ShopRegion::getId ,shopRegion.getId());
        lqw.like(StringUtils.isNotBlank(shopRegion.getRegionCode()),ShopRegion::getRegionCode ,shopRegion.getRegionCode());
        lqw.like(StringUtils.isNotBlank(shopRegion.getRegionName()),ShopRegion::getRegionName ,shopRegion.getRegionName());
        lqw.like(StringUtils.isNotBlank(shopRegion.getParentCode()),ShopRegion::getParentCode ,shopRegion.getParentCode());
        lqw.eq(shopRegion.getRegionLevel() != null, ShopRegion::getRegionLevel ,shopRegion.getRegionLevel());
        lqw.eq(shopRegion.getIsRemote() != null, ShopRegion::getIsRemote ,shopRegion.getIsRemote());
        lqw.eq(shopRegion.getSortOrder() != null, ShopRegion::getSortOrder ,shopRegion.getSortOrder());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopRegion::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopRegion::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ShopRegion::getId);
        return shopRegionService.list(lqw);
    }

    /**
     * 导入 地区信息表 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "shopRegion::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ShopRegion.class, new ReadListener<ShopRegion>() {
                @Override
                public void invoke(ShopRegion shopRegion, AnalysisContext analysisContext) {
                    shopRegionService.save(shopRegion);
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