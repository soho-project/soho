package work.soho.open.biz.controller;

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
import work.soho.api.admin.annotation.Node;
import work.soho.open.biz.domain.OpenApp;
import work.soho.open.biz.service.OpenAppService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.api.admin.vo.OptionVo;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.api.admin.vo.TreeNodeVo;
/**
 * 开放平台appController
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/openApp" )
public class OpenAppController {

    private final OpenAppService openAppService;

    /**
     * 查询开放平台app列表
     */
    @GetMapping("/list")
    @Node(value = "openApp::list", name = "开放平台app;;列表")
    public R<PageSerializable<OpenApp>> list(OpenApp openApp, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenApp> lqw = new LambdaQueryWrapper<OpenApp>();
        lqw.eq(openApp.getId() != null, OpenApp::getId ,openApp.getId());
        lqw.like(StringUtils.isNotBlank(openApp.getName()),OpenApp::getName ,openApp.getName());
        lqw.like(StringUtils.isNotBlank(openApp.getAppKey()),OpenApp::getAppKey ,openApp.getAppKey());
        lqw.like(StringUtils.isNotBlank(openApp.getUpdatedTime()),OpenApp::getUpdatedTime ,openApp.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenApp::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenApp::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<OpenApp> list = openAppService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取开放平台app详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "openApp::getInfo", name = "开放平台app;;详细信息")
    public R<OpenApp> getInfo(@PathVariable("id" ) Long id) {
        return R.success(openAppService.getById(id));
    }

    /**
     * 新增开放平台app
     */
    @PostMapping
    @Node(value = "openApp::add", name = "开放平台app;;新增")
    public R<Boolean> add(@RequestBody OpenApp openApp) {
        return R.success(openAppService.save(openApp));
    }

    /**
     * 修改开放平台app
     */
    @PutMapping
    @Node(value = "openApp::edit", name = "开放平台app;;修改")
    public R<Boolean> edit(@RequestBody OpenApp openApp) {
        return R.success(openAppService.updateById(openApp));
    }

    /**
     * 删除开放平台app
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "openApp::remove", name = "开放平台app;;删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(openAppService.removeByIds(Arrays.asList(ids)));
    }
}