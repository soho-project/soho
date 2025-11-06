package work.soho.shop.biz.controller;

import cn.hutool.core.lang.Assert;
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
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;
import work.soho.shop.biz.domain.ShopProductCategory;
import work.soho.shop.biz.service.ShopProductCategoryService;

import java.util.*;
import java.util.stream.Collectors;
/**
 * 商品分类Controller
 *
 * @author fang
 */
@Api(tags = "商品分类")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/admin/shopProductCategory" )
public class ShopProductCategoryController {

    private final ShopProductCategoryService shopProductCategoryService;

    /**
     * 查询商品分类列表
     */
    @GetMapping("/list")
    @Node(value = "shopProductCategory::list", name = "获取 商品分类 列表")
    public R<PageSerializable<ShopProductCategory>> list(ShopProductCategory shopProductCategory, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopProductCategory> lqw = new LambdaQueryWrapper<>();
        lqw.eq(shopProductCategory.getId() != null, ShopProductCategory::getId ,shopProductCategory.getId());
        lqw.like(StringUtils.isNotBlank(shopProductCategory.getName()),ShopProductCategory::getName ,shopProductCategory.getName());
        lqw.eq(shopProductCategory.getParentId() != null, ShopProductCategory::getParentId ,shopProductCategory.getParentId());
        lqw.eq(shopProductCategory.getLevel() != null, ShopProductCategory::getLevel ,shopProductCategory.getLevel());
        lqw.eq(shopProductCategory.getSortOrder() != null, ShopProductCategory::getSortOrder ,shopProductCategory.getSortOrder());
        lqw.eq(shopProductCategory.getStatus() != null, ShopProductCategory::getStatus ,shopProductCategory.getStatus());
        lqw.like(StringUtils.isNotBlank(shopProductCategory.getIcon()),ShopProductCategory::getIcon ,shopProductCategory.getIcon());
        lqw.like(StringUtils.isNotBlank(shopProductCategory.getDescription()),ShopProductCategory::getDescription ,shopProductCategory.getDescription());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopProductCategory::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopProductCategory::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(shopProductCategory.getUpdatedTime() != null, ShopProductCategory::getUpdatedTime ,shopProductCategory.getUpdatedTime());
        lqw.orderByDesc(ShopProductCategory::getId);
        List<ShopProductCategory> list = shopProductCategoryService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取商品分类详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "shopProductCategory::getInfo", name = "获取 商品分类 详细信息")
    public R<ShopProductCategory> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopProductCategoryService.getById(id));
    }

    /**
     * 新增商品分类
     */
    @PostMapping
    @Node(value = "shopProductCategory::add", name = "新增 商品分类")
    public R<Boolean> add(@RequestBody ShopProductCategory shopProductCategory) {
        // 确认层级
        Assert.notNull(shopProductCategory.getParentId(), "请选择父级分类");
        if(shopProductCategory.getParentId() == 0) {
            shopProductCategory.setLevel(1);
        } else {
            ShopProductCategory parent = shopProductCategoryService.getById(shopProductCategory.getParentId());
            Assert.notNull(parent, "父级分类不存在");
            shopProductCategory.setLevel(parent.getLevel() + 1);
        }
        return R.success(shopProductCategoryService.save(shopProductCategory));
    }

    /**
     * 修改商品分类
     */
    @PutMapping
    @Node(value = "shopProductCategory::edit", name = "修改 商品分类")
    public R<Boolean> edit(@RequestBody ShopProductCategory shopProductCategory) {
        return R.success(shopProductCategoryService.updateById(shopProductCategory));
    }

    /**
     * 删除商品分类
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "shopProductCategory::remove", name = "删除 商品分类")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        List<ShopProductCategory> oldData = shopProductCategoryService.listByIds(Arrays.asList(ids));
        if(oldData.size() != ids.length) {
            return R.error("数据不存在");
        }
        //检查是否有子节点
        LambdaQueryWrapper<ShopProductCategory> lqw = new LambdaQueryWrapper<>();
        lqw.in(ShopProductCategory::getParentId, Arrays.asList(ids));
        lqw.notIn(ShopProductCategory::getId, Arrays.asList(ids));
        List<ShopProductCategory> list = shopProductCategoryService.list(lqw);
        if (!list.isEmpty()) {
            return R.error("还有子节点，无法删除");
        }
        return R.success(shopProductCategoryService.removeByIds(Arrays.asList(ids)));
    }

    @GetMapping("tree")
    public R<List<TreeNodeVo>> tree() {
        List<ShopProductCategory> list = shopProductCategoryService.list();
        List<TreeNodeVo<Long, Long, Long, String>> listVo = list.stream().map(item->{
            return new TreeNodeVo<>(item.getId(), item.getId(), item.getParentId(), item.getName());
        }).collect(Collectors.toList());

        Map<Long, List<TreeNodeVo>> mapVo = new HashMap<>();
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
        return R.success(mapVo.get(0L));
    }

    /**
     * 导出 商品分类 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ShopProductCategory.class)
    @Node(value = "shopProductCategory::exportExcel", name = "导出 商品分类 Excel")
    public Object exportExcel(ShopProductCategory shopProductCategory, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ShopProductCategory> lqw = new LambdaQueryWrapper<ShopProductCategory>();
        lqw.eq(shopProductCategory.getId() != null, ShopProductCategory::getId ,shopProductCategory.getId());
        lqw.like(StringUtils.isNotBlank(shopProductCategory.getName()),ShopProductCategory::getName ,shopProductCategory.getName());
        lqw.eq(shopProductCategory.getParentId() != null, ShopProductCategory::getParentId ,shopProductCategory.getParentId());
        lqw.eq(shopProductCategory.getLevel() != null, ShopProductCategory::getLevel ,shopProductCategory.getLevel());
        lqw.eq(shopProductCategory.getSortOrder() != null, ShopProductCategory::getSortOrder ,shopProductCategory.getSortOrder());
        lqw.eq(shopProductCategory.getStatus() != null, ShopProductCategory::getStatus ,shopProductCategory.getStatus());
        lqw.like(StringUtils.isNotBlank(shopProductCategory.getIcon()),ShopProductCategory::getIcon ,shopProductCategory.getIcon());
        lqw.like(StringUtils.isNotBlank(shopProductCategory.getDescription()),ShopProductCategory::getDescription ,shopProductCategory.getDescription());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopProductCategory::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopProductCategory::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(shopProductCategory.getUpdatedTime() != null, ShopProductCategory::getUpdatedTime ,shopProductCategory.getUpdatedTime());
        lqw.orderByDesc(ShopProductCategory::getId);
        return shopProductCategoryService.list(lqw);
    }

    /**
     * 导入 商品分类 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "shopProductCategory::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ShopProductCategory.class, new ReadListener<ShopProductCategory>() {
                @Override
                public void invoke(ShopProductCategory shopProductCategory, AnalysisContext analysisContext) {
                    shopProductCategoryService.save(shopProductCategory);
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