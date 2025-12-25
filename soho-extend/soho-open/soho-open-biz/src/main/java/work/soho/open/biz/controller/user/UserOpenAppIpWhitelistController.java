package work.soho.open.biz.controller.user;

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
import work.soho.open.biz.domain.OpenAppIpWhitelist;
import work.soho.open.biz.service.OpenAppIpWhitelistService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
/**
 * Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/openAppIpWhitelist" )
public class UserOpenAppIpWhitelistController {

    private final OpenAppIpWhitelistService openAppIpWhitelistService;

    /**
     * 查询列表
     */
    @GetMapping("/list")
    @Node(value = "user::openAppIpWhitelist::list", name = "获取  列表")
    public R<PageSerializable<OpenAppIpWhitelist>> list(OpenAppIpWhitelist openAppIpWhitelist, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenAppIpWhitelist> lqw = new LambdaQueryWrapper<OpenAppIpWhitelist>();
        lqw.eq(openAppIpWhitelist.getId() != null, OpenAppIpWhitelist::getId ,openAppIpWhitelist.getId());
        lqw.eq(openAppIpWhitelist.getAppId() != null, OpenAppIpWhitelist::getAppId ,openAppIpWhitelist.getAppId());
        lqw.like(StringUtils.isNotBlank(openAppIpWhitelist.getIp()),OpenAppIpWhitelist::getIp ,openAppIpWhitelist.getIp());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenAppIpWhitelist::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenAppIpWhitelist::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<OpenAppIpWhitelist> list = openAppIpWhitelistService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "user::openAppIpWhitelist::getInfo", name = "获取  详细信息")
    public R<OpenAppIpWhitelist> getInfo(@PathVariable("id" ) Long id) {
        return R.success(openAppIpWhitelistService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    @Node(value = "user::openAppIpWhitelist::add", name = "新增 ")
    public R<Boolean> add(@RequestBody OpenAppIpWhitelist openAppIpWhitelist) {
        return R.success(openAppIpWhitelistService.save(openAppIpWhitelist));
    }

    /**
     * 修改
     */
    @PutMapping
    @Node(value = "user::openAppIpWhitelist::edit", name = "修改 ")
    public R<Boolean> edit(@RequestBody OpenAppIpWhitelist openAppIpWhitelist) {
        return R.success(openAppIpWhitelistService.updateById(openAppIpWhitelist));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "user::openAppIpWhitelist::remove", name = "删除 ")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(openAppIpWhitelistService.removeByIds(Arrays.asList(ids)));
    }
}