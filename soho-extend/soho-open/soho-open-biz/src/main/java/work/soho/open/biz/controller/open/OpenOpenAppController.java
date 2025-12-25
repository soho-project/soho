package work.soho.open.biz.controller.open;

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
import work.soho.open.biz.domain.OpenApp;
import work.soho.open.biz.service.OpenAppService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

;
/**
 * APPController
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/open/openApp" )
public class OpenOpenAppController {

    private final OpenAppService openAppService;

    /**
     * 查询APP列表
     */
    @GetMapping("/list")
    @Node(value = "open::openApp::list", name = "获取 APP 列表")
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
     * 获取APP详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "open::openApp::getInfo", name = "获取 APP 详细信息")
    public R<OpenApp> getInfo(@PathVariable("id" ) Long id) {
        return R.success(openAppService.getById(id));
    }

    /**
     * 新增APP
     */
    @PostMapping
    @Node(value = "open::openApp::add", name = "新增 APP")
    public R<Boolean> add(@RequestBody OpenApp openApp) {
        return R.success(openAppService.save(openApp));
    }

    /**
     * 修改APP
     */
    @PutMapping
    @Node(value = "open::openApp::edit", name = "修改 APP")
    public R<Boolean> edit(@RequestBody OpenApp openApp) {
        return R.success(openAppService.updateById(openApp));
    }

    /**
     * 删除APP
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "open::openApp::remove", name = "删除 APP")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(openAppService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该APP 选项
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "open::openApp::options", name = "获取 APP 选项")
    public R<List<OptionVo<Long, String>>> options() {
        List<OpenApp> list = openAppService.list();
        List<OptionVo<Long, String>> options = new ArrayList<>();

        HashMap<Long, String> map = new HashMap<>();
        for(OpenApp item: list) {
            OptionVo<Long, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getId());
            optionVo.setLabel(item.getAppName());
            options.add(optionVo);
        }
        return R.success(options);
    }
}