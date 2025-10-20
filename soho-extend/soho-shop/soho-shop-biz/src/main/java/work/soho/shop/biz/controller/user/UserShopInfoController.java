package work.soho.shop.biz.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.vo.OptionVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.shop.biz.domain.ShopInfo;
import work.soho.shop.biz.service.ShopInfoService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

;
/**
 * 店铺信息Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/shopInfo" )
public class UserShopInfoController {

    private final ShopInfoService shopInfoService;

    /**
     * 查询店铺信息列表
     */
    @GetMapping("/list")
    @Node(value = "user::shopInfo::list", name = "获取 店铺信息 列表")
    public R<PageSerializable<ShopInfo>> list(ShopInfo shopInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
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
        List<ShopInfo> list = shopInfoService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取店铺信息详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "user::shopInfo::getInfo", name = "获取 店铺信息 详细信息")
    public R<ShopInfo> getInfo(@PathVariable("id" ) Long id) {
        return R.success(shopInfoService.getById(id));
    }

    /**
     * 新增店铺信息
     */
    @PostMapping
    @Node(value = "user::shopInfo::add", name = "新增 店铺信息")
    public R<Boolean> add(@RequestBody ShopInfo shopInfo) {
        return R.success(shopInfoService.save(shopInfo));
    }

    /**
     * 修改店铺信息
     */
    @PutMapping
    @Node(value = "user::shopInfo::edit", name = "修改 店铺信息")
    public R<Boolean> edit(@RequestBody ShopInfo shopInfo) {
        return R.success(shopInfoService.updateById(shopInfo));
    }

    /**
     * 删除店铺信息
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "user::shopInfo::remove", name = "删除 店铺信息")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopInfoService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该店铺信息 选项
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "user::shopInfo::options", name = "获取 店铺信息 选项")
    public R<List<OptionVo<Integer, String>>> options() {
        List<ShopInfo> list = shopInfoService.list();
        List<OptionVo<Integer, String>> options = new ArrayList<>();

        HashMap<Integer, String> map = new HashMap<>();
        for(ShopInfo item: list) {
            OptionVo<Integer, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getId());
            optionVo.setLabel(item.getName());
            options.add(optionVo);
        }
        return R.success(options);
    }
}