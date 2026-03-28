package work.soho.ai.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.soho.ai.biz.domain.AiMemberCard;
import work.soho.ai.biz.domain.AiMemberCardRedeemCode;
import work.soho.ai.biz.domain.AiUserMemberCard;
import work.soho.ai.biz.mapper.AiMemberCardRedeemCodeMapper;
import work.soho.ai.biz.service.AiMemberCardRedeemCodeService;
import work.soho.ai.biz.service.AiMemberCardService;
import work.soho.ai.biz.service.AiUserMemberCardService;
import work.soho.common.core.util.StringUtils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class AiMemberCardRedeemCodeServiceImpl extends ServiceImpl<AiMemberCardRedeemCodeMapper, AiMemberCardRedeemCode>
        implements AiMemberCardRedeemCodeService {

    private static final char[] CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    private static final SecureRandom RANDOM = new SecureRandom();

    private final AiMemberCardService aiMemberCardService;
    private final AiUserMemberCardService aiUserMemberCardService;

    public AiMemberCardRedeemCodeServiceImpl(AiMemberCardService aiMemberCardService,
                                             AiUserMemberCardService aiUserMemberCardService) {
        this.aiMemberCardService = aiMemberCardService;
        this.aiUserMemberCardService = aiUserMemberCardService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchGenerateResult batchGenerate(Long memberCardId, Integer count, String batchNo,
                                             LocalDateTime expireTime, String remark) {
        if (memberCardId == null || memberCardId <= 0) {
            throw new IllegalArgumentException("会员卡ID不能为空");
        }
        int size = count == null ? 0 : count;
        if (size <= 0 || size > 5000) {
            throw new IllegalArgumentException("生成数量必须在1-5000之间");
        }

        AiMemberCard card = aiMemberCardService.getById(memberCardId);
        if (card == null || card.getId() == null || !Integer.valueOf(1).equals(card.getStatus())) {
            throw new IllegalArgumentException("会员卡不存在或已禁用");
        }

        LocalDateTime now = LocalDateTime.now();
        String resolvedBatchNo = StringUtils.isBlank(batchNo)
                ? "MC" + now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                : batchNo.trim();

        List<AiMemberCardRedeemCode> saveList = new ArrayList<>(size);
        Set<String> generated = new HashSet<>();
        int maxAttempts = size * 20;
        int attempts = 0;

        while (saveList.size() < size && attempts < maxAttempts) {
            attempts++;
            String code = generateCode();
            if (generated.contains(code)) {
                continue;
            }
            long exists = count(new LambdaQueryWrapper<AiMemberCardRedeemCode>()
                    .eq(AiMemberCardRedeemCode::getRedeemCode, code));
            if (exists > 0) {
                continue;
            }
            generated.add(code);

            AiMemberCardRedeemCode item = new AiMemberCardRedeemCode();
            item.setMemberCardId(memberCardId);
            item.setBatchNo(resolvedBatchNo);
            item.setRedeemCode(code);
            item.setStatus(0);
            item.setExpireTime(expireTime);
            item.setRemark(remark);
            item.setCreatedTime(now);
            item.setUpdatedTime(now);
            saveList.add(item);
        }

        if (saveList.size() < size) {
            throw new IllegalStateException("兑换码生成失败，请重试");
        }
        saveBatch(saveList);
        return new BatchGenerateResult(resolvedBatchNo, saveList.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RedeemResult redeem(Long userId, String redeemCode) {
        if (userId == null || userId <= 0) {
            return new RedeemResult(false, "用户未登录");
        }
        if (StringUtils.isBlank(redeemCode)) {
            return new RedeemResult(false, "兑换码不能为空");
        }

        String normalizedCode = redeemCode.trim().toUpperCase(Locale.ROOT);
        AiMemberCardRedeemCode code = getOne(new LambdaQueryWrapper<AiMemberCardRedeemCode>()
                .eq(AiMemberCardRedeemCode::getRedeemCode, normalizedCode)
                .last("limit 1"));
        if (code == null) {
            return new RedeemResult(false, "兑换码不存在");
        }
        if (!Integer.valueOf(0).equals(code.getStatus())) {
            return new RedeemResult(false, "兑换码已使用或已失效");
        }
        LocalDateTime now = LocalDateTime.now();
        if (code.getExpireTime() != null && now.isAfter(code.getExpireTime())) {
            return new RedeemResult(false, "兑换码已过期");
        }

        AiMemberCard card = aiMemberCardService.getById(code.getMemberCardId());
        if (card == null || card.getId() == null || !Integer.valueOf(1).equals(card.getStatus())) {
            return new RedeemResult(false, "会员卡不存在或已禁用");
        }

        boolean lockSuccess = update(new LambdaUpdateWrapper<AiMemberCardRedeemCode>()
                .eq(AiMemberCardRedeemCode::getId, code.getId())
                .eq(AiMemberCardRedeemCode::getStatus, 0)
                .set(AiMemberCardRedeemCode::getStatus, 1)
                .set(AiMemberCardRedeemCode::getUsedByUserId, userId)
                .set(AiMemberCardRedeemCode::getUsedTime, now)
                .set(AiMemberCardRedeemCode::getUpdatedTime, now));
        if (!lockSuccess) {
            return new RedeemResult(false, "兑换码已被使用");
        }

        Integer validityDays = card.getValidityDays() == null ? 30 : card.getValidityDays();
        AiUserMemberCard userCard = new AiUserMemberCard();
        userCard.setUserId(userId);
        userCard.setMemberCardId(card.getId());
        userCard.setStatus(1);
        userCard.setPriority(0);
        userCard.setIsSelected(true);
        userCard.setStartTime(now);
        userCard.setEndTime(now.plusDays(Math.max(1, validityDays)));
        userCard.setActivatedTime(now);
        userCard.setSource("redeem_code");
        userCard.setBizNo(normalizedCode);
        userCard.setCreatedTime(now);
        userCard.setUpdatedTime(now);

        aiUserMemberCardService.update(new LambdaUpdateWrapper<AiUserMemberCard>()
                .eq(AiUserMemberCard::getUserId, userId)
                .set(AiUserMemberCard::getIsSelected, false)
                .set(AiUserMemberCard::getUpdatedTime, now));
        aiUserMemberCardService.save(userCard);

        update(new LambdaUpdateWrapper<AiMemberCardRedeemCode>()
                .eq(AiMemberCardRedeemCode::getId, code.getId())
                .set(AiMemberCardRedeemCode::getUserMemberCardId, userCard.getId())
                .set(AiMemberCardRedeemCode::getUpdatedTime, now));

        return new RedeemResult(true, "兑换成功");
    }

    private String generateCode() {
        int len = 16;
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(CODE_CHARS[RANDOM.nextInt(CODE_CHARS.length)]);
        }
        return sb.toString();
    }
}
