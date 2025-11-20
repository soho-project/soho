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
import work.soho.shop.biz.domain.ShopUserReviews;
import work.soho.shop.biz.service.ShopUserReviewsService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * 电商用户评论表Controller
 *
 * @author fang
 */
@Api(tags = "电商用户评论表")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/admin/shopUserReviews" )
public class ShopUserReviewsController {

    private final ShopUserReviewsService shopUserReviewsService;
    private final AdminDictApiService adminDictApiService;

    /**
     * 查询电商用户评论表列表
     */
    @GetMapping("/list")
    @Node(value = "shopUserReviews::list", name = "获取 电商用户评论表 列表")
    public R<PageSerializable<ShopUserReviews>> list(ShopUserReviews shopUserReviews, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ShopUserReviews> lqw = new LambdaQueryWrapper<>();
        lqw.eq(shopUserReviews.getId() != null, ShopUserReviews::getId ,shopUserReviews.getId());
        lqw.eq(shopUserReviews.getProductId() != null, ShopUserReviews::getProductId ,shopUserReviews.getProductId());
        lqw.eq(shopUserReviews.getUserId() != null, ShopUserReviews::getUserId ,shopUserReviews.getUserId());
        lqw.like(StringUtils.isNotBlank(shopUserReviews.getOrderId()),ShopUserReviews::getOrderId ,shopUserReviews.getOrderId());
        lqw.eq(shopUserReviews.getParentId() != null, ShopUserReviews::getParentId ,shopUserReviews.getParentId());
        lqw.eq(shopUserReviews.getRating() != null, ShopUserReviews::getRating ,shopUserReviews.getRating());
        lqw.like(StringUtils.isNotBlank(shopUserReviews.getTitle()),ShopUserReviews::getTitle ,shopUserReviews.getTitle());
        lqw.like(StringUtils.isNotBlank(shopUserReviews.getContent()),ShopUserReviews::getContent ,shopUserReviews.getContent());
        lqw.like(StringUtils.isNotBlank(shopUserReviews.getContentHtml()),ShopUserReviews::getContentHtml ,shopUserReviews.getContentHtml());
        lqw.like(StringUtils.isNotBlank(shopUserReviews.getMediaUrls()),ShopUserReviews::getMediaUrls ,shopUserReviews.getMediaUrls());
        lqw.eq(shopUserReviews.getIsAnonymous() != null, ShopUserReviews::getIsAnonymous ,shopUserReviews.getIsAnonymous());
        lqw.eq(shopUserReviews.getLikeCount() != null, ShopUserReviews::getLikeCount ,shopUserReviews.getLikeCount());
        lqw.eq(shopUserReviews.getReportCount() != null, ShopUserReviews::getReportCount ,shopUserReviews.getReportCount());
        lqw.eq(shopUserReviews.getStatus() != null, ShopUserReviews::getStatus ,shopUserReviews.getStatus());
        lqw.eq(shopUserReviews.getAuditAdminId() != null, ShopUserReviews::getAuditAdminId ,shopUserReviews.getAuditAdminId());
        lqw.like(StringUtils.isNotBlank(shopUserReviews.getAuditRemark()),ShopUserReviews::getAuditRemark ,shopUserReviews.getAuditRemark());
        lqw.eq(shopUserReviews.getAuditTime() != null, ShopUserReviews::getAuditTime ,shopUserReviews.getAuditTime());
        lqw.eq(shopUserReviews.getIsTop() != null, ShopUserReviews::getIsTop ,shopUserReviews.getIsTop());
        lqw.like(StringUtils.isNotBlank(shopUserReviews.getExtraData()),ShopUserReviews::getExtraData ,shopUserReviews.getExtraData());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopUserReviews::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopUserReviews::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(shopUserReviews.getUpdatedTime() != null, ShopUserReviews::getUpdatedTime ,shopUserReviews.getUpdatedTime());
        lqw.orderByDesc(ShopUserReviews::getId);
        List<ShopUserReviews> list = shopUserReviewsService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取电商用户评论表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "shopUserReviews::getInfo", name = "获取 电商用户评论表 详细信息")
    public R<ShopUserReviews> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopUserReviewsService.getById(id));
    }

    /**
     * 新增电商用户评论表
     */
    @PostMapping
    @Node(value = "shopUserReviews::add", name = "新增 电商用户评论表")
    public R<Boolean> add(@RequestBody ShopUserReviews shopUserReviews) {
        return R.success(shopUserReviewsService.save(shopUserReviews));
    }

    /**
     * 修改电商用户评论表
     */
    @PutMapping
    @Node(value = "shopUserReviews::edit", name = "修改 电商用户评论表")
    public R<Boolean> edit(@RequestBody ShopUserReviews shopUserReviews) {
        return R.success(shopUserReviewsService.updateById(shopUserReviews));
    }

    /**
     * 删除电商用户评论表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "shopUserReviews::remove", name = "删除 电商用户评论表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopUserReviewsService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 导出 电商用户评论表 Excel
     */
    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "excel.xls", modelClass = ShopUserReviews.class)
    @Node(value = "shopUserReviews::exportExcel", name = "导出 电商用户评论表 Excel")
    public Object exportExcel(ShopUserReviews shopUserReviews, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ShopUserReviews> lqw = new LambdaQueryWrapper<ShopUserReviews>();
        lqw.eq(shopUserReviews.getId() != null, ShopUserReviews::getId ,shopUserReviews.getId());
        lqw.eq(shopUserReviews.getProductId() != null, ShopUserReviews::getProductId ,shopUserReviews.getProductId());
        lqw.eq(shopUserReviews.getUserId() != null, ShopUserReviews::getUserId ,shopUserReviews.getUserId());
        lqw.like(StringUtils.isNotBlank(shopUserReviews.getOrderId()),ShopUserReviews::getOrderId ,shopUserReviews.getOrderId());
        lqw.eq(shopUserReviews.getParentId() != null, ShopUserReviews::getParentId ,shopUserReviews.getParentId());
        lqw.eq(shopUserReviews.getRating() != null, ShopUserReviews::getRating ,shopUserReviews.getRating());
        lqw.like(StringUtils.isNotBlank(shopUserReviews.getTitle()),ShopUserReviews::getTitle ,shopUserReviews.getTitle());
        lqw.like(StringUtils.isNotBlank(shopUserReviews.getContent()),ShopUserReviews::getContent ,shopUserReviews.getContent());
        lqw.like(StringUtils.isNotBlank(shopUserReviews.getContentHtml()),ShopUserReviews::getContentHtml ,shopUserReviews.getContentHtml());
        lqw.like(StringUtils.isNotBlank(shopUserReviews.getMediaUrls()),ShopUserReviews::getMediaUrls ,shopUserReviews.getMediaUrls());
        lqw.eq(shopUserReviews.getIsAnonymous() != null, ShopUserReviews::getIsAnonymous ,shopUserReviews.getIsAnonymous());
        lqw.eq(shopUserReviews.getLikeCount() != null, ShopUserReviews::getLikeCount ,shopUserReviews.getLikeCount());
        lqw.eq(shopUserReviews.getReportCount() != null, ShopUserReviews::getReportCount ,shopUserReviews.getReportCount());
        lqw.eq(shopUserReviews.getStatus() != null, ShopUserReviews::getStatus ,shopUserReviews.getStatus());
        lqw.eq(shopUserReviews.getAuditAdminId() != null, ShopUserReviews::getAuditAdminId ,shopUserReviews.getAuditAdminId());
        lqw.like(StringUtils.isNotBlank(shopUserReviews.getAuditRemark()),ShopUserReviews::getAuditRemark ,shopUserReviews.getAuditRemark());
        lqw.eq(shopUserReviews.getAuditTime() != null, ShopUserReviews::getAuditTime ,shopUserReviews.getAuditTime());
        lqw.eq(shopUserReviews.getIsTop() != null, ShopUserReviews::getIsTop ,shopUserReviews.getIsTop());
        lqw.like(StringUtils.isNotBlank(shopUserReviews.getExtraData()),ShopUserReviews::getExtraData ,shopUserReviews.getExtraData());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ShopUserReviews::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ShopUserReviews::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(shopUserReviews.getUpdatedTime() != null, ShopUserReviews::getUpdatedTime ,shopUserReviews.getUpdatedTime());
        lqw.orderByDesc(ShopUserReviews::getId);
        return shopUserReviewsService.list(lqw);
    }

    /**
     * 导入 电商用户评论表 Excel
     *
     * @param file
     * @return
     */
    @PostMapping("/importExcel")
    @Node(value = "shopUserReviews::importExcel", name = "导入 自动化样例 Excel")
    public R importExcel(@RequestParam(value = "file")MultipartFile file) {
        try {
            EasyExcelFactory.read(file.getInputStream(), ShopUserReviews.class, new ReadListener<ShopUserReviews>() {
                @Override
                public void invoke(ShopUserReviews shopUserReviews, AnalysisContext analysisContext) {
                    shopUserReviewsService.save(shopUserReviews);
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