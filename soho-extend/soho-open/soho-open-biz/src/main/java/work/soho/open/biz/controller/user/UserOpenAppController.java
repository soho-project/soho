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
import work.soho.open.biz.domain.OpenApp;
import work.soho.open.biz.service.OpenAppService;
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
@RequestMapping("/user/openApp" )
public class UserOpenAppController {

    private final OpenAppService openAppService;

    /**
     * 查询列表
     */
    @GetMapping("/list")
    @Node(value = "user::openApp::list", name = "获取  列表")
    public R<PageSerializable<OpenApp>> list(OpenApp openApp, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenApp> lqw = new LambdaQueryWrapper<OpenApp>();
        lqw.eq(openApp.getId() != null, OpenApp::getId ,openApp.getId());
        lqw.eq(openApp.getUserId() != null, OpenApp::getUserId ,openApp.getUserId());
        lqw.like(StringUtils.isNotBlank(openApp.getAppName()),OpenApp::getAppName ,openApp.getAppName());
        lqw.like(StringUtils.isNotBlank(openApp.getAppKey()),OpenApp::getAppKey ,openApp.getAppKey());
        lqw.like(StringUtils.isNotBlank(openApp.getAppSecret()),OpenApp::getAppSecret ,openApp.getAppSecret());
        lqw.eq(openApp.getStatus() != null, OpenApp::getStatus ,openApp.getStatus());
        lqw.eq(openApp.getQpsLimit() != null, OpenApp::getQpsLimit ,openApp.getQpsLimit());
        lqw.like(StringUtils.isNotBlank(openApp.getRemark()),OpenApp::getRemark ,openApp.getRemark());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenApp::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenApp::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(openApp.getUpdatedTime() != null, OpenApp::getUpdatedTime ,openApp.getUpdatedTime());
        List<OpenApp> list = openAppService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "user::openApp::getInfo", name = "获取  详细信息")
    public R<OpenApp> getInfo(@PathVariable("id" ) Long id) {
        return R.success(openAppService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    @Node(value = "user::openApp::add", name = "新增 ")
    public R<Boolean> add(@RequestBody OpenApp openApp) {
        return R.success(openAppService.save(openApp));
    }

    /**
     * 修改
     */
    @PutMapping
    @Node(value = "user::openApp::edit", name = "修改 ")
    public R<Boolean> edit(@RequestBody OpenApp openApp) {
        return R.success(openAppService.updateById(openApp));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "user::openApp::remove", name = "删除 ")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(openAppService.removeByIds(Arrays.asList(ids)));
    }
}