package work.soho.shop.biz.controller.user;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.result.R;
import work.soho.shop.biz.config.ShopSysConfig;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/shop/user/shopInfoIndex" )
public class UserShopIndexController {
    private final ShopSysConfig shopSysConfig;

    /**
     * 获取轮播图
     *
     * @return
     */
    @RequestMapping("/queryIndexConfig")
    public R<Map<String, Object>> queryBanner() {
        HashMap<String, Object> map = new HashMap<>();
        String banner = shopSysConfig.getIndexBanner();
        List<String> list = Arrays.asList(banner.split(";"));
        map.put("banner", list);
        // 获取首页动作
        String indexAction = shopSysConfig.getIndexActions();
        JSONArray jsonObject = JSONUtil.parseArray(indexAction);
        map.put("indexAction", jsonObject);
        return R.success(map);
    }
}
