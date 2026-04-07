package work.soho.ai.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.ai.biz.domain.AiMemberCard;
import work.soho.ai.biz.domain.AiUserMemberCard;
import work.soho.ai.biz.service.AiMemberCardService;
import work.soho.ai.biz.service.AiUserMemberCardService;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Api(tags = "AI 用户会员卡管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/admin/userMemberCard")
public class AiUserMemberCardController {
    private final AiUserMemberCardService aiUserMemberCardService;
    private final AiMemberCardService aiMemberCardService;

    @GetMapping("/list")
    @Node(value = "ai::userMemberCard::list", name = "获取 AI 用户会员卡列表")
    @ApiOperation("获取 AI 用户会员卡列表")
    public R<PageSerializable<AiUserMemberCard>> list(AiUserMemberCard query, BetweenCreatedTimeRequest betweenCreatedTimeRequest) {
        PageUtils.startPage();
        LambdaQueryWrapper<AiUserMemberCard> lqw = new LambdaQueryWrapper<>();
        lqw.eq(query.getUserId() != null, AiUserMemberCard::getUserId, query.getUserId());
        lqw.eq(query.getMemberCardId() != null, AiUserMemberCard::getMemberCardId, query.getMemberCardId());
        lqw.eq(query.getStatus() != null, AiUserMemberCard::getStatus, query.getStatus());
        lqw.eq(query.getIsSelected() != null, AiUserMemberCard::getIsSelected, query.getIsSelected());
        lqw.ge(betweenCreatedTimeRequest != null && betweenCreatedTimeRequest.getStartTime() != null,
                AiUserMemberCard::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest != null && betweenCreatedTimeRequest.getEndTime() != null,
                AiUserMemberCard::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(AiUserMemberCard::getId);
        List<AiUserMemberCard> list = aiUserMemberCardService.list(lqw);
        aiUserMemberCardService.fillUsageInfo(list);
        return R.success(new PageSerializable<>(list));
    }

    @GetMapping("/{id}")
    @Node(value = "ai::userMemberCard::getInfo", name = "获取 AI 用户会员卡详情")
    @ApiOperation("获取 AI 用户会员卡详情")
    public R<AiUserMemberCard> getInfo(@PathVariable Long id) {
        AiUserMemberCard item = aiUserMemberCardService.getById(id);
        if (item != null) {
            aiUserMemberCardService.fillUsageInfo(Collections.singletonList(item));
        }
        return R.success(item);
    }

    @PostMapping
    @Node(value = "ai::userMemberCard::add", name = "新增 AI 用户会员卡")
    @ApiOperation("新增 AI 用户会员卡")
    public R<Boolean> add(@RequestBody AiUserMemberCard item) {
        if (StringUtils.isBlank(item.getNo())) {
            item.setNo(generateCardNo());
        } else {
            item.setNo(item.getNo().trim());
        }
        return R.success(aiUserMemberCardService.save(item));
    }

    @PutMapping
    @Node(value = "ai::userMemberCard::edit", name = "修改 AI 用户会员卡")
    @ApiOperation("修改 AI 用户会员卡")
    public R<Boolean> edit(@RequestBody AiUserMemberCard item) {
        return R.success(aiUserMemberCardService.updateById(item));
    }

    @DeleteMapping("/{ids}")
    @Node(value = "ai::userMemberCard::remove", name = "删除 AI 用户会员卡")
    @ApiOperation("删除 AI 用户会员卡")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(aiUserMemberCardService.removeByIds(Arrays.asList(ids)));
    }

    @PostMapping("/grant")
    @Node(value = "ai::userMemberCard::grant", name = "发放 AI 用户会员卡")
    @ApiOperation("发放 AI 用户会员卡")
    public R<Boolean> grant(@RequestBody GrantRequest request) {
        AiMemberCard memberCard = aiMemberCardService.getById(request.getMemberCardId());
        if (memberCard == null || memberCard.getId() == null) {
            return R.error("会员卡不存在");
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = request.getStartTime() == null ? now : request.getStartTime();
        Integer validityDays = memberCard.getValidityDays() == null ? 30 : memberCard.getValidityDays();
        LocalDateTime end = request.getEndTime() == null ? start.plusDays(Math.max(1, validityDays)) : request.getEndTime();
        if (!end.isAfter(start)) {
            return R.error("结束时间必须大于开始时间");
        }

        AiUserMemberCard item = new AiUserMemberCard();
        item.setUserId(request.getUserId());
        item.setMemberCardId(request.getMemberCardId());
        item.setNo(StringUtils.isNotBlank(request.getNo()) ? request.getNo().trim() : generateCardNo());
        item.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        item.setPriority(request.getPriority() == null ? 0 : request.getPriority());
        item.setIsSelected(Boolean.TRUE.equals(request.getIsSelected()));
        item.setStartTime(start);
        item.setEndTime(end);
        item.setActivatedTime(request.getActivatedTime() == null ? now : request.getActivatedTime());
        item.setSource(request.getSource() == null ? "admin" : request.getSource());
        item.setBizNo(request.getBizNo());
        item.setCreatedTime(now);
        item.setUpdatedTime(now);
        return R.success(aiUserMemberCardService.save(item));
    }

    private String generateCardNo() {
        return "MC" + IDGeneratorUtils.uuid32().substring(0, 16).toUpperCase();
    }

    @Data
    public static class GrantRequest {
        private Long userId;
        private Long memberCardId;
        private Integer status;
        private Integer priority;
        private Boolean isSelected;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startTime;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endTime;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime activatedTime;
        private String source;
        private String bizNo;
        private String no;
    }
}
