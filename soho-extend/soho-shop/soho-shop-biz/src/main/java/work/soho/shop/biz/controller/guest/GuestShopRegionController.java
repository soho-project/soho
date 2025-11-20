package work.soho.shop.biz.controller.guest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.shop.biz.domain.ShopRegion;
import work.soho.shop.biz.service.ShopRegionService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

;
/**
 * 地区信息表Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/guest/shopRegion" )
public class GuestShopRegionController {

    private final ShopRegionService shopRegionService;

    /**
     * 查询地区信息表列表
     */
    @GetMapping("/list")
    @Node(value = "guest::shopRegion::list", name = "获取 地区信息表 列表")
    public R<PageSerializable<ShopRegion>> list(ShopRegion shopRegion, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
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
        List<ShopRegion> list = shopRegionService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取地区信息表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "guest::shopRegion::getInfo", name = "获取 地区信息表 详细信息")
    public R<ShopRegion> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopRegionService.getById(id));
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
        return R.success(mapVo.get(0));
    }
}