package work.soho.ai.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.service.EmailApiService;
import work.soho.ai.biz.domain.AiMemberCardRedeemCode;
import work.soho.ai.biz.service.AiMemberCardRedeemCodeService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Api(tags = "AI 会员卡兑换码管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/admin/memberCardRedeemCode")
public class AiMemberCardRedeemCodeController {
    private static final String DEFAULT_TEMPLATE_NAME = "ai-member-card-redeem-code";

    private final AiMemberCardRedeemCodeService aiMemberCardRedeemCodeService;
    private final EmailApiService emailApiService;

    @GetMapping("/list")
    @Node(value = "ai::memberCardRedeemCode::list", name = "获取 AI 会员卡兑换码列表")
    @ApiOperation("获取 AI 会员卡兑换码列表")
    public R<PageSerializable<AiMemberCardRedeemCode>> list(AiMemberCardRedeemCode query,
                                                             BetweenCreatedTimeRequest betweenCreatedTimeRequest) {
        PageUtils.startPage();
        LambdaQueryWrapper<AiMemberCardRedeemCode> lqw = buildQuery(query, betweenCreatedTimeRequest);
        lqw.orderByDesc(AiMemberCardRedeemCode::getId);
        List<AiMemberCardRedeemCode> list = aiMemberCardRedeemCodeService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    @PostMapping("/batchGenerate")
    @Node(value = "ai::memberCardRedeemCode::batchGenerate", name = "批量生成 AI 会员卡兑换码")
    @ApiOperation("批量生成 AI 会员卡兑换码")
    public R<AiMemberCardRedeemCodeService.BatchGenerateResult> batchGenerate(@RequestBody BatchGenerateRequest request) {
        try {
            return R.success(aiMemberCardRedeemCodeService.batchGenerate(
                    request.getMemberCardId(),
                    request.getCount(),
                    request.getBatchNo(),
                    request.getExpireTime(),
                    request.getRemark()));
        } catch (IllegalArgumentException e) {
            return R.error(e.getMessage());
        }
    }

    @PostMapping("/batchMarkSold")
    @Node(value = "ai::memberCardRedeemCode::batchMarkSold", name = "批量标记 AI 会员卡兑换码已售出")
    @ApiOperation("批量标记 AI 会员卡兑换码已售出")
    public R<Integer> batchMarkSold(@RequestBody BatchMarkSoldRequest request) {
        if (request == null || request.getIds() == null || request.getIds().length == 0) {
            return R.error("ids不能为空");
        }
        int affected = aiMemberCardRedeemCodeService.batchMarkSold(Arrays.asList(request.getIds()));
        return R.success(affected);
    }

    @PostMapping("/sendEmailAndMarkSold")
    @Node(value = "ai::memberCardRedeemCode::sendEmailAndMarkSold", name = "发送 AI 会员卡兑换码邮件并标记已售")
    @ApiOperation("发送 AI 会员卡兑换码邮件并标记已售")
    public R<SendEmailAndMarkSoldResult> sendEmailAndMarkSold(@RequestBody SendEmailAndMarkSoldRequest request) {
        if (request == null || request.getIds() == null || request.getIds().length == 0) {
            return R.error("ids不能为空");
        }
        if (StringUtils.isBlank(request.getTo())) {
            return R.error("收件人邮箱不能为空");
        }
        String templateName = StringUtils.isBlank(request.getTemplateName())
                ? DEFAULT_TEMPLATE_NAME
                : request.getTemplateName();
        Set<Long> deduplicatedIds = new LinkedHashSet<>();
        for (Long id : request.getIds()) {
            if (id != null && id > 0) {
                deduplicatedIds.add(id);
            }
        }
        if (deduplicatedIds.isEmpty()) {
            return R.error("ids不能为空");
        }
        List<Long> ids = new ArrayList<>(deduplicatedIds);
        List<AiMemberCardRedeemCode> sellableCodes = aiMemberCardRedeemCodeService.list(new LambdaQueryWrapper<AiMemberCardRedeemCode>()
                .in(AiMemberCardRedeemCode::getId, ids)
                .eq(AiMemberCardRedeemCode::getStatus, 0)
                .eq(AiMemberCardRedeemCode::getSoldStatus, 0)
                .orderByAsc(AiMemberCardRedeemCode::getId));
        if (sellableCodes.size() != ids.size()) {
            return R.error("所选兑换码中包含已售出、已使用或不存在的数据");
        }

        Map<String, Object> model = buildSendEmailModel(sellableCodes);
        try {
            emailApiService.sendEmail(request.getTo(), templateName, model);
        } catch (Exception e) {
            return R.error("邮件发送失败: " + e.getMessage());
        }
        int affected = aiMemberCardRedeemCodeService.batchMarkSold(ids);
        return R.success(new SendEmailAndMarkSoldResult(request.getTo(), templateName,
                sellableCodes.size(), affected));
    }

    @GetMapping("/exportExcel")
    @ExcelExport(fileName = "member_card_redeem_code.xls", modelClass = AiMemberCardRedeemCode.class)
    @Node(value = "ai::memberCardRedeemCode::exportExcel", name = "导出 AI 会员卡兑换码 Excel")
    @ApiOperation("导出 AI 会员卡兑换码 Excel")
    public Object exportExcel(AiMemberCardRedeemCode query, BetweenCreatedTimeRequest betweenCreatedTimeRequest) {
        LambdaQueryWrapper<AiMemberCardRedeemCode> lqw = buildQuery(query, betweenCreatedTimeRequest);
        lqw.orderByDesc(AiMemberCardRedeemCode::getId);
        return aiMemberCardRedeemCodeService.list(lqw);
    }

    private LambdaQueryWrapper<AiMemberCardRedeemCode> buildQuery(AiMemberCardRedeemCode query,
                                                                   BetweenCreatedTimeRequest betweenCreatedTimeRequest) {
        LambdaQueryWrapper<AiMemberCardRedeemCode> lqw = new LambdaQueryWrapper<>();
        lqw.eq(query.getId() != null, AiMemberCardRedeemCode::getId, query.getId());
        lqw.eq(query.getMemberCardId() != null, AiMemberCardRedeemCode::getMemberCardId, query.getMemberCardId());
        lqw.like(StringUtils.isNotBlank(query.getBatchNo()), AiMemberCardRedeemCode::getBatchNo, query.getBatchNo());
        lqw.like(StringUtils.isNotBlank(query.getRedeemCode()), AiMemberCardRedeemCode::getRedeemCode, query.getRedeemCode());
        lqw.eq(query.getStatus() != null, AiMemberCardRedeemCode::getStatus, query.getStatus());
        lqw.eq(query.getSoldStatus() != null, AiMemberCardRedeemCode::getSoldStatus, query.getSoldStatus());
        lqw.eq(query.getUsedByUserId() != null, AiMemberCardRedeemCode::getUsedByUserId, query.getUsedByUserId());
        lqw.ge(betweenCreatedTimeRequest != null && betweenCreatedTimeRequest.getStartTime() != null,
                AiMemberCardRedeemCode::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest != null && betweenCreatedTimeRequest.getEndTime() != null,
                AiMemberCardRedeemCode::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        return lqw;
    }

    private Map<String, Object> buildSendEmailModel(List<AiMemberCardRedeemCode> codes) {
        if (codes == null || codes.isEmpty()) {
            return Collections.emptyMap();
        }
        List<String> redeemCodeList = new ArrayList<>();
        Set<String> batchNoSet = new LinkedHashSet<>();
        for (AiMemberCardRedeemCode code : codes) {
            redeemCodeList.add(code.getRedeemCode());
            if (StringUtils.isNotBlank(code.getBatchNo())) {
                batchNoSet.add(code.getBatchNo());
            }
        }
        String joinedCodes = String.join("\n", redeemCodeList);
        String joinedBatchNo = String.join(",", batchNoSet);
        Map<String, Object> model = new HashMap<>();
        model.put("count", redeemCodeList.size());
        model.put("batchNo", joinedBatchNo);
        model.put("redeemCodeList", redeemCodeList);
        model.put("redeemCodes", joinedCodes);
        return model;
    }

    @Data
    public static class BatchGenerateRequest {
        private Long memberCardId;
        private Integer count;
        private String batchNo;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime expireTime;
        private String remark;
    }

    @Data
    public static class BatchMarkSoldRequest {
        private Long[] ids;
    }

    @Data
    public static class SendEmailAndMarkSoldRequest {
        private Long[] ids;
        private String to;
        /**
         * admin_email_template.name
         */
        private String templateName;
    }

    @Data
    public static class SendEmailAndMarkSoldResult {
        private final String to;
        private final String templateName;
        private final Integer requestedCount;
        private final Integer markedSoldCount;
    }
}
