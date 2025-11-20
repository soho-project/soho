package work.soho.shop.biz.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.shop.api.vo.ProductReviewVo;
import work.soho.shop.biz.domain.ShopUserReviews;
import work.soho.shop.biz.enums.ShopUserReviewsEnums;
import work.soho.shop.biz.service.ShopUserReviewsService;
import work.soho.user.api.dto.UserInfoDto;
import work.soho.user.api.service.UserApiService;

import java.util.List;
import java.util.stream.Collectors;

;
/**
 * 电商用户评论表Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/user/shopUserReviews" )
public class UserShopUserReviewsController {

    private final ShopUserReviewsService shopUserReviewsService;
    private final UserApiService userInfoService;

    /**
     * 查询电商用户评论表列表
     */
    @GetMapping("/list")
    @Node(value = "user::shopUserReviews::list", name = "获取 电商用户评论表 列表")
    public R<PageSerializable<ShopUserReviews>> list(ShopUserReviews shopUserReviews, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
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
        List<ShopUserReviews> list = shopUserReviewsService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取电商用户评论表详细信息
     */
    @ApiOperation("获取电商用户评论表详细信息")
    @GetMapping("/getByProductId/{productId}")
    public R<PageSerializable<ProductReviewVo>> getByProductId(@PathVariable Long productId) {
        LambdaQueryWrapper<ShopUserReviews> lqw = new LambdaQueryWrapper<ShopUserReviews>();
        lqw.eq(ShopUserReviews::getProductId, productId);
        lqw.eq(ShopUserReviews::getStatus, ShopUserReviewsEnums.Status.APPROVED.getId());
        lqw.orderByDesc(ShopUserReviews::getCreatedTime);
        PageUtils.startPage();
        List<ShopUserReviews> list = shopUserReviewsService.list(lqw);

        Page<ProductReviewVo> page = new Page<ProductReviewVo>();
        page.setTotal(((Page)list).getTotal());

        List<ProductReviewVo> productReviewVos = list.stream().map(item -> {
            ProductReviewVo productReviewVo = new ProductReviewVo();
            productReviewVo.setId(item.getId());
            productReviewVo.setContent(item.getContent());
            productReviewVo.setRating(item.getRating());
            productReviewVo.setTime(item.getCreatedTime());
            productReviewVo.setImages(item.getMediaUrls());

            // 获取用户信息
            UserInfoDto userInfoDto = userInfoService.getUserById(item.getUserId());
            productReviewVo.setUser(userInfoDto.getUsername());
            productReviewVo.setAvatar(userInfoDto.getAvatar());

            return productReviewVo;
        }).collect(Collectors.toList());
        return R.success(new PageSerializable<ProductReviewVo>(productReviewVos));
    }

    /**
     * 获取电商用户评论表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "user::shopUserReviews::getInfo", name = "获取 电商用户评论表 详细信息")
    public R<ShopUserReviews> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopUserReviewsService.getById(id));
    }

    /**
     * 新增电商用户评论表
     */
    @PostMapping
    @Node(value = "user::shopUserReviews::add", name = "新增 电商用户评论表")
    public R<Boolean> add(@RequestBody ShopUserReviews shopUserReviews) {
        return R.success(shopUserReviewsService.save(shopUserReviews));
    }

}