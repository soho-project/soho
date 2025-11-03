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
import work.soho.shop.biz.domain.ShopProductLike;
import work.soho.shop.biz.service.ShopProductLikeService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 用户商品喜欢记录表Controller
 *
 * @author fang
 */
@Api(tags = "用户商品喜欢记录表")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/admin/shopProductLike" )
public class ShopProductLikeController {

    private final ShopProductLikeService shopProductLikeService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询用户商品喜欢记录表列表
     */
    @GetMapping("/list")
    @Node(value = "shopProductLike::list", name = "获取 用户商品喜欢记录表 列表")
    public R<PageSerializable<ShopProductLike>> list(ShopProductLike shopProductLike, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopProductLike> lqw = new LambdaQueryWrapper<>();
        lqw.eq(shopProductLike.getId() != null, ShopProductLike::getId ,shopProductLike.getId());
        lqw.eq(shopProductLike.getUserId() != null, ShopProductLike::getUserId ,shopProductLike.getUserId());
        lqw.eq(shopProductLike.getProductId() != null, ShopProductLike::getProductId ,shopProductLike.getProductId());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopProductLike::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopProductLike::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(shopProductLike.getUpdatedTime() != null, ShopProductLike::getUpdatedTime ,shopProductLike.getUpdatedTime());
        lqw.orderByDesc(ShopProductLike::getId);
        List<ShopProductLike> list = shopProductLikeService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取用户商品喜欢记录表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "shopProductLike::getInfo", name = "获取 用户商品喜欢记录表 详细信息")
    public R<ShopProductLike> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopProductLikeService.getById(id));
    }

    /**
     * 新增用户商品喜欢记录表
     */
    @PostMapping
    @Node(value = "shopProductLike::add", name = "新增 用户商品喜欢记录表")
    public R<Boolean> add(@RequestBody ShopProductLike shopProductLike) {
        return R.success(shopProductLikeService.save(shopProductLike));
    }

    /**
     * 修改用户商品喜欢记录表
     */
    @PutMapping
    @Node(value = "shopProductLike::edit", name = "修改 用户商品喜欢记录表")
    public R<Boolean> edit(@RequestBody ShopProductLike shopProductLike) {
        return R.success(shopProductLikeService.updateById(shopProductLike));
    }

    /**
     * 删除用户商品喜欢记录表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "shopProductLike::remove", name = "删除 用户商品喜欢记录表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopProductLikeService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 用户商品喜欢记录表 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ShopProductLike.class)
    @Node(value = "shopProductLike::exportExcel", name = "导出 用户商品喜欢记录表 Excel")
    public Object exportExcel(ShopProductLike shopProductLike, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ShopProductLike> lqw = new LambdaQueryWrapper<ShopProductLike>();
        lqw.eq(shopProductLike.getId() != null, ShopProductLike::getId ,shopProductLike.getId());
        lqw.eq(shopProductLike.getUserId() != null, ShopProductLike::getUserId ,shopProductLike.getUserId());
        lqw.eq(shopProductLike.getProductId() != null, ShopProductLike::getProductId ,shopProductLike.getProductId());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopProductLike::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopProductLike::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(shopProductLike.getUpdatedTime() != null, ShopProductLike::getUpdatedTime ,shopProductLike.getUpdatedTime());
        lqw.orderByDesc(ShopProductLike::getId);
        return shopProductLikeService.list(lqw);
    }

    /**
     * 导入 用户商品喜欢记录表 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "shopProductLike::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ShopProductLike.class, new ReadListener<ShopProductLike>() {
                @Override
                public void invoke(ShopProductLike shopProductLike, AnalysisContext analysisContext) {
                    shopProductLikeService.save(shopProductLike);
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