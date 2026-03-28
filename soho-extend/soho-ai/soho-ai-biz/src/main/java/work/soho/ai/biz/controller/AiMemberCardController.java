package work.soho.ai.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.ai.biz.domain.AiMemberCard;
import work.soho.ai.biz.service.AiMemberCardService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;

import java.util.Arrays;
import java.util.List;

@Api(tags = "AI 会员卡模板管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/admin/memberCard")
public class AiMemberCardController {
    private final AiMemberCardService aiMemberCardService;

    @GetMapping("/list")
    @Node(value = "ai::memberCard::list", name = "获取 AI 会员卡模板列表")
    @ApiOperation("获取 AI 会员卡模板列表")
    public R<PageSerializable<AiMemberCard>> list(AiMemberCard query, BetweenCreatedTimeRequest betweenCreatedTimeRequest) {
        PageUtils.startPage();
        LambdaQueryWrapper<AiMemberCard> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotBlank(query.getName()), AiMemberCard::getName, query.getName());
        lqw.eq(StringUtils.isNotBlank(query.getCardType()), AiMemberCard::getCardType, query.getCardType());
        lqw.eq(StringUtils.isNotBlank(query.getLimitMode()), AiMemberCard::getLimitMode, query.getLimitMode());
        lqw.eq(query.getStatus() != null, AiMemberCard::getStatus, query.getStatus());
        lqw.ge(betweenCreatedTimeRequest != null && betweenCreatedTimeRequest.getStartTime() != null,
                AiMemberCard::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest != null && betweenCreatedTimeRequest.getEndTime() != null,
                AiMemberCard::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(AiMemberCard::getSort).orderByDesc(AiMemberCard::getId);
        List<AiMemberCard> list = aiMemberCardService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    @GetMapping("/{id}")
    @Node(value = "ai::memberCard::getInfo", name = "获取 AI 会员卡模板详情")
    @ApiOperation("获取 AI 会员卡模板详情")
    public R<AiMemberCard> getInfo(@PathVariable Long id) {
        return R.success(aiMemberCardService.getById(id));
    }

    @PostMapping
    @Node(value = "ai::memberCard::add", name = "新增 AI 会员卡模板")
    @ApiOperation("新增 AI 会员卡模板")
    public R<Boolean> add(@RequestBody AiMemberCard item) {
        return R.success(aiMemberCardService.save(item));
    }

    @PutMapping
    @Node(value = "ai::memberCard::edit", name = "修改 AI 会员卡模板")
    @ApiOperation("修改 AI 会员卡模板")
    public R<Boolean> edit(@RequestBody AiMemberCard item) {
        return R.success(aiMemberCardService.updateById(item));
    }

    @DeleteMapping("/{ids}")
    @Node(value = "ai::memberCard::remove", name = "删除 AI 会员卡模板")
    @ApiOperation("删除 AI 会员卡模板")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(aiMemberCardService.removeByIds(Arrays.asList(ids)));
    }
}
