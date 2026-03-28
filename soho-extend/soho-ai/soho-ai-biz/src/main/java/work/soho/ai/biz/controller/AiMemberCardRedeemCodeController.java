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
import work.soho.ai.biz.domain.AiMemberCardRedeemCode;
import work.soho.ai.biz.service.AiMemberCardRedeemCodeService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.excel.annotation.ExcelExport;
import work.soho.common.security.annotation.Node;

import java.time.LocalDateTime;
import java.util.List;

@Api(tags = "AI 会员卡兑换码管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/admin/memberCardRedeemCode")
public class AiMemberCardRedeemCodeController {
    private final AiMemberCardRedeemCodeService aiMemberCardRedeemCodeService;

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
        lqw.eq(query.getUsedByUserId() != null, AiMemberCardRedeemCode::getUsedByUserId, query.getUsedByUserId());
        lqw.ge(betweenCreatedTimeRequest != null && betweenCreatedTimeRequest.getStartTime() != null,
                AiMemberCardRedeemCode::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest != null && betweenCreatedTimeRequest.getEndTime() != null,
                AiMemberCardRedeemCode::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        return lqw;
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
}
