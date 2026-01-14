package work.soho.open.biz.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.vo.OptionVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.open.biz.domain.OpenApp;
import work.soho.open.biz.enums.OpenAppEnums;
import work.soho.open.biz.service.OpenAppService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

;

/**
 * Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/open/user/openApp" )
public class UserOpenAppController {

    private final OpenAppService openAppService;

    /**
     * 查询列表
     */
    @GetMapping("/list")
    @Node(value = "user::openApp::list", name = "获取  列表")
    public R<PageSerializable<OpenApp>> list(OpenApp openApp, BetweenCreatedTimeRequest betweenCreatedTimeRequest, @AuthenticationPrincipal SohoUserDetails userDetails)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenApp> lqw = new LambdaQueryWrapper<OpenApp>();
        lqw.eq(openApp.getId() != null, OpenApp::getId ,openApp.getId());
        lqw.eq(OpenApp::getUserId, userDetails.getId());
        lqw.like(StringUtils.isNotBlank(openApp.getAppName()),OpenApp::getAppName ,openApp.getAppName());
        lqw.like(StringUtils.isNotBlank(openApp.getAppDesc()),OpenApp::getAppDesc ,openApp.getAppDesc());
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
    public R<OpenApp> getInfo(@PathVariable("id" ) Long id, @AuthenticationPrincipal SohoUserDetails userDetails) {
        OpenApp openApp = openAppService.getById(id);
        if (openApp.getUserId().equals(userDetails.getId())) {
            return R.error("数据不存在");
        }
        return R.success(openApp);
    }

    /**
     * 新增
     */
    @PostMapping
    @Node(value = "user::openApp::add", name = "新增 ")
    public R<Boolean> add(@RequestBody OpenApp openApp, @AuthenticationPrincipal SohoUserDetails userDetails) {
        openApp.setUserId(userDetails.getId());
        openApp.setAppKey(IDGeneratorUtils.snowflake().toString());
        openApp.setAppSecret(IDGeneratorUtils.uuid32());
        openApp.setStatus(OpenAppEnums.Status.DISABLED.getId());
        return R.success(openAppService.save(openApp));
    }

    /**
     * 修改
     */
    @PutMapping
    @Node(value = "user::openApp::edit", name = "修改 ")
    public R<Boolean> edit(@RequestBody OpenApp openApp, @AuthenticationPrincipal SohoUserDetails userDetails) {
        OpenApp oldOpenApp = openAppService.getById(openApp.getId());
        Assert.notNull(oldOpenApp, "数据不存在");
        if(!oldOpenApp.getUserId().equals(userDetails.getId())) {
            return R.error("无权限");
        }
        return R.success(openAppService.updateById(openApp));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "user::openApp::remove", name = "删除 ")
    public R<Boolean> remove(@PathVariable Long[] ids, @AuthenticationPrincipal SohoUserDetails userDetails) {

        List<OpenApp> oldList = openAppService.listByIds(Arrays.asList(ids));
        // 检查是否为当前登录用户的数据
        for(OpenApp item: oldList) {
            if(!item.getUserId().equals(userDetails.getId())) {
                return R.error("无权限");
            }
        }
        return R.success(openAppService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该 选项
     *
     * @return
     */
    @GetMapping("options")
    @Node(value = "user::openApp::options", name = "获取  选项")
    public R<List<OptionVo<Long, String>>> options(@AuthenticationPrincipal SohoUserDetails userDetails) {
        LambdaQueryWrapper<OpenApp> lqw = new LambdaQueryWrapper<OpenApp>();
        lqw.eq(OpenApp::getUserId, userDetails.getId());
        List<OpenApp> list = openAppService.list(lqw);
        List<OptionVo<Long, String>> options = new ArrayList<>();

        for(OpenApp item: list) {
            OptionVo<Long, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getId());
            optionVo.setLabel(item.getAppName());
            options.add(optionVo);
        }
        return R.success(options);
    }

    /**
     * 同级用户应用总数
     */
    @GetMapping("count")
    @Node(value = "user::openApp::count", name = "获取 同级用户应用总数")
    public R<Integer> count(@AuthenticationPrincipal SohoUserDetails userDetails) {
        List<OpenApp> list = openAppService.list(new LambdaQueryWrapper<OpenApp>().eq(OpenApp::getUserId, userDetails.getId()));
        return R.success(list.size());
    }
}