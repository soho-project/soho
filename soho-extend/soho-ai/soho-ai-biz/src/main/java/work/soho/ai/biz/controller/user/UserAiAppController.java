package work.soho.ai.biz.controller.user;

import java.time.LocalDateTime;
import work.soho.common.core.util.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.*;
import lombok.RequiredArgsConstructor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import work.soho.ai.biz.domain.AiApp;
import work.soho.ai.biz.service.AiAppService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.admin.api.vo.OptionVo;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.admin.api.service.AdminDictApiService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import work.soho.common.security.userdetails.SohoUserDetails;
import org.springframework.util.Assert;

/**
 * AI应用Controller
 *
 * @author i
 */
@Api(value = "user AI应用", tags = "user AI应用")
@RequiredArgsConstructor
@RestController
@RequestMapping("ai/user/aiApp" )
public class UserAiAppController {

    private final AiAppService aiAppService;

    /**
     * 查询AI应用列表
     */
    @GetMapping("/list")
    @Node(value = "user::aiApp::list", name = "获取 AI应用 列表")
    @ApiOperation(value = "user 获取 AI应用 列表", notes = "user 获取 AI应用 列表")
    public R<PageSerializable<AiApp>> list(AiApp aiApp, BetweenCreatedTimeRequest betweenCreatedTimeRequest, @AuthenticationPrincipal SohoUserDetails userDetails)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<AiApp> lqw = new LambdaQueryWrapper<AiApp>();
        lqw.like(StringUtils.isNotBlank(aiApp.getCode()),AiApp::getCode ,aiApp.getCode());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, AiApp::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, AiApp::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.like(StringUtils.isNotBlank(aiApp.getDescription()),AiApp::getDescription ,aiApp.getDescription());
        lqw.eq(aiApp.getId() != null, AiApp::getId ,aiApp.getId());
        lqw.eq(aiApp.getStatus() != null, AiApp::getStatus ,aiApp.getStatus());
        lqw.like(StringUtils.isNotBlank(aiApp.getSystemPrompt()),AiApp::getSystemPrompt ,aiApp.getSystemPrompt());
        lqw.like(StringUtils.isNotBlank(aiApp.getTitle()),AiApp::getTitle ,aiApp.getTitle());
        lqw.eq(aiApp.getUpdatedTime() != null, AiApp::getUpdatedTime ,aiApp.getUpdatedTime());
        List<AiApp> list = aiAppService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取AI应用详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "user::aiApp::getInfo", name = "获取 AI应用 详细信息")
    @ApiOperation(value = "user 获取 AI应用 详细信息", notes = "user 获取 AI应用 详细信息")
    public R<AiApp> getInfo(@PathVariable("id" ) Long id, @AuthenticationPrincipal SohoUserDetails userDetails) {
        AiApp aiApp = aiAppService.getById(id);
        return R.success(aiApp);
    }

    /**
     * 新增AI应用
     */
    @PostMapping
    @Node(value = "user::aiApp::add", name = "新增 AI应用")
    @ApiOperation(value = "user 新增 AI应用", notes = "user 新增 AI应用")
    public R<Boolean> add(@RequestBody AiApp aiApp, @AuthenticationPrincipal SohoUserDetails userDetails) {
        return R.success(aiAppService.save(aiApp));
    }

    /**
     * 修改AI应用
     */
    @PutMapping
    @Node(value = "user::aiApp::edit", name = "修改 AI应用")
    @ApiOperation(value = "user 修改 AI应用", notes = "user 修改 AI应用")
    public R<Boolean> edit(@RequestBody AiApp aiApp, @AuthenticationPrincipal SohoUserDetails userDetails) {
        AiApp oldAiApp = aiAppService.getById(aiApp.getId());
        Assert.notNull(oldAiApp, "数据不存在");
        return R.success(aiAppService.updateById(aiApp));
    }

    /**
     * 删除AI应用
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "user::aiApp::remove", name = "删除 AI应用")
    @ApiOperation(value = "user 删除 AI应用", notes = "user 删除 AI应用")
    public R<Boolean> remove(@PathVariable Long[] ids, @AuthenticationPrincipal SohoUserDetails userDetails) {
        return R.success(aiAppService.removeByIds(Arrays.asList(ids)));
    }
}