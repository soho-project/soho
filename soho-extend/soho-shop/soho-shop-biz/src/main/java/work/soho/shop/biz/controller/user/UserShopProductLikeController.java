package work.soho.shop.biz.controller.user;

import java.time.LocalDateTime;
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
import work.soho.common.security.annotation.Node;;
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
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/shopProductLike" )
public class UserShopProductLikeController {

    private final ShopProductLikeService shopProductLikeService;

    /**
     * 新增用户商品喜欢记录表
     */
    @PostMapping
    @Node(value = "user::shopProductLike::add", name = "新增 用户商品喜欢记录表")
    public R<Boolean> add(@RequestBody ShopProductLike shopProductLike) {
        return R.success(shopProductLikeService.save(shopProductLike));
    }

    /**
     * 删除用户商品喜欢记录表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "user::shopProductLike::remove", name = "删除 用户商品喜欢记录表")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(shopProductLikeService.removeByIds(Arrays.asList(ids)));
    }
}