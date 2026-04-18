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
import work.soho.ai.biz.domain.AiModelInfo;
import work.soho.ai.biz.service.AiModelInfoService;
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
 * AI模型Controller
 *
 * @author fang
 */
@Api(value = "user AI模型", tags = "user AI模型")
@RequiredArgsConstructor
@RestController
@RequestMapping("ai/user/aiModelInfo" )
public class UserAiModelInfoController {

    private final AiModelInfoService aiModelInfoService;

    /**
     * 查询AI模型列表
     */
    @GetMapping("/list")
    @Node(value = "user::aiModelInfo::list", name = "获取 AI模型 列表")
    @ApiOperation(value = "user 获取 AI模型 列表", notes = "user 获取 AI模型 列表")
    public R<PageSerializable<AiModelInfo>> list(AiModelInfo aiModelInfo, BetweenCreatedTimeRequest betweenCreatedTimeRequest, @AuthenticationPrincipal SohoUserDetails userDetails)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<AiModelInfo> lqw = new LambdaQueryWrapper<AiModelInfo>();
        lqw.eq(aiModelInfo.getId() != null, AiModelInfo::getId ,aiModelInfo.getId());
        lqw.like(StringUtils.isNotBlank(aiModelInfo.getModelName()),AiModelInfo::getModelName ,aiModelInfo.getModelName());
        lqw.like(StringUtils.isNotBlank(aiModelInfo.getModelDesc()),AiModelInfo::getModelDesc ,aiModelInfo.getModelDesc());
        lqw.like(StringUtils.isNotBlank(aiModelInfo.getModelDetail()),AiModelInfo::getModelDetail ,aiModelInfo.getModelDetail());
        lqw.eq(StringUtils.isNotBlank(aiModelInfo.getModelTag()), AiModelInfo::getModelTag ,aiModelInfo.getModelTag());
        lqw.eq(aiModelInfo.getStatus() != null, AiModelInfo::getStatus ,aiModelInfo.getStatus());
        lqw.eq(aiModelInfo.getPromptPrice() != null, AiModelInfo::getPromptPrice ,aiModelInfo.getPromptPrice());
        lqw.eq(aiModelInfo.getCompletionPrice() != null, AiModelInfo::getCompletionPrice ,aiModelInfo.getCompletionPrice());
        lqw.eq(aiModelInfo.getFixedRequestPrice() != null, AiModelInfo::getFixedRequestPrice ,aiModelInfo.getFixedRequestPrice());
        lqw.eq(aiModelInfo.getSort() != null, AiModelInfo::getSort ,aiModelInfo.getSort());
        lqw.eq(aiModelInfo.getUpdatedTime() != null, AiModelInfo::getUpdatedTime ,aiModelInfo.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, AiModelInfo::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, AiModelInfo::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<AiModelInfo> list = aiModelInfoService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取AI模型详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "user::aiModelInfo::getInfo", name = "获取 AI模型 详细信息")
    @ApiOperation(value = "user 获取 AI模型 详细信息", notes = "user 获取 AI模型 详细信息")
    public R<AiModelInfo> getInfo(@PathVariable("id" ) Long id, @AuthenticationPrincipal SohoUserDetails userDetails) {
        AiModelInfo aiModelInfo = aiModelInfoService.getById(id);
        return R.success(aiModelInfo);
    }

}
