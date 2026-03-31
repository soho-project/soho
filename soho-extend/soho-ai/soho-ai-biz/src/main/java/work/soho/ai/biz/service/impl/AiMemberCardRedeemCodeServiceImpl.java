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
import work.soho.admin.api.service.EmailApiService;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.wallet.api.enums.WalletLogEnums;
import work.soho.wallet.api.enums.WalletTypeNameEnums;
import work.soho.wallet.api.service.WalletInfoApiService;
import work.soho.wallet.biz.domain.WalletInfo;
import work.soho.wallet.biz.service.WalletInfoService;

import java.security.SecureRandom;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

@Service
public class AiMemberCardRedeemCodeServiceImpl extends ServiceImpl<AiMemberCardRedeemCodeMapper, AiMemberCardRedeemCode>
        implements AiMemberCardRedeemCodeService {

    private static final char[] CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String DEFAULT_TEMPLATE_NAME = "ai-member-card-redeem-code";
    private static final int DEFAULT_WALLET_TYPE_ID = 1;

    private final AiMemberCardService aiMemberCardService;
    private final AiUserMemberCardService aiUserMemberCardService;
    private final WalletInfoService walletInfoService;
    private final WalletInfoApiService walletInfoApiService;
    private final EmailApiService emailApiService;

    public AiMemberCardRedeemCodeServiceImpl(AiMemberCardService aiMemberCardService,
                                             AiUserMemberCardService aiUserMemberCardService,
                                             WalletInfoService walletInfoService,
                                             WalletInfoApiService walletInfoApiService,
                                             EmailApiService emailApiService) {
        this.aiMemberCardService = aiMemberCardService;
        this.aiUserMemberCardService = aiUserMemberCardService;
        this.walletInfoService = walletInfoService;
        this.walletInfoApiService = walletInfoApiService;
        this.emailApiService = emailApiService;
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
            item.setSoldStatus(0);
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
        userCard.setNo(generateCardNo());
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseRedeemCodeResult purchaseByMemberCardName(Long userId, String memberCardName,
                                                             String email) {
        if (userId == null || userId <= 0) {
            return new PurchaseRedeemCodeResult(false, "用户未登录", memberCardName, null, BigDecimal.ZERO,
                    DEFAULT_WALLET_TYPE_ID, null);
        }
        if (StringUtils.isBlank(memberCardName)) {
            return new PurchaseRedeemCodeResult(false, "会员卡名称不能为空", null, null, BigDecimal.ZERO,
                    DEFAULT_WALLET_TYPE_ID, null);
        }

        String normalizedCardName = memberCardName.trim();
        int resolvedWalletTypeId = WalletTypeNameEnums.RMB.getId();
        LocalDateTime now = LocalDateTime.now();

        AiMemberCard memberCard = aiMemberCardService.getOne(new LambdaQueryWrapper<AiMemberCard>()
                .eq(AiMemberCard::getName, normalizedCardName)
                .eq(AiMemberCard::getStatus, 1)
                .last("limit 1"));
        if (memberCard == null || memberCard.getId() == null) {
            return new PurchaseRedeemCodeResult(false, "会员卡不存在或已禁用", normalizedCardName, null, BigDecimal.ZERO,
                    resolvedWalletTypeId, null);
        }
        BigDecimal amount = memberCard.getSalePrice() == null ? BigDecimal.ZERO : memberCard.getSalePrice();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new PurchaseRedeemCodeResult(false, "会员卡价格未配置", normalizedCardName, null, amount,
                    resolvedWalletTypeId, null);
        }

        WalletInfo walletInfo = walletInfoService.getByUserIdAndType(userId, resolvedWalletTypeId);
        if (walletInfo == null) {
            return new PurchaseRedeemCodeResult(false, "钱包不存在", normalizedCardName, null, amount,
                    resolvedWalletTypeId, null);
        }
        if (walletInfo.getAmount() == null || walletInfo.getAmount().compareTo(amount) < 0) {
            return new PurchaseRedeemCodeResult(false, "钱包余额不足", normalizedCardName, null, amount,
                    resolvedWalletTypeId, null);
        }

        AiMemberCardRedeemCode code = getOne(new LambdaQueryWrapper<AiMemberCardRedeemCode>()
                .eq(AiMemberCardRedeemCode::getMemberCardId, memberCard.getId())
                .eq(AiMemberCardRedeemCode::getStatus, 0)
                .eq(AiMemberCardRedeemCode::getSoldStatus, 0)
                .and(wrapper -> wrapper.isNull(AiMemberCardRedeemCode::getExpireTime)
                        .or()
                        .gt(AiMemberCardRedeemCode::getExpireTime, now))
                .orderByAsc(AiMemberCardRedeemCode::getId)
                .last("limit 1"));
        if (code == null) {
            return new PurchaseRedeemCodeResult(false, "兑换码库存不足", normalizedCardName, null, amount,
                    resolvedWalletTypeId, null);
        }

        boolean lockSuccess = update(new LambdaUpdateWrapper<AiMemberCardRedeemCode>()
                .eq(AiMemberCardRedeemCode::getId, code.getId())
                .eq(AiMemberCardRedeemCode::getStatus, 0)
                .eq(AiMemberCardRedeemCode::getSoldStatus, 0)
                .set(AiMemberCardRedeemCode::getSoldStatus, 1)
                .set(AiMemberCardRedeemCode::getUpdatedTime, now));
        if (!lockSuccess) {
            return new PurchaseRedeemCodeResult(false, "兑换码已售罄，请重试", normalizedCardName, null, amount,
                    resolvedWalletTypeId, null);
        }

        Long walletLogId = walletInfoApiService.changeWalletAmount(
                userId,
                resolvedWalletTypeId,
                WalletLogEnums.BizId.PAY_ORDER.getId(),
                "ai_member_card_code_" + IDGeneratorUtils.uuid32(),
                amount.negate(),
                "AI会员卡兑换码购买 cardName=" + normalizedCardName + ", redeemCode=" + code.getRedeemCode()
        );
        if (StringUtils.isNotBlank(email)) {
            Map<String, Object> model = new HashMap<>();
            model.put("count", 1);
            model.put("batchNo", code.getBatchNo());
            model.put("redeemCodeList", List.of(code.getRedeemCode()));
            model.put("redeemCodes", code.getRedeemCode());
            emailApiService.sendEmail(email.trim(), DEFAULT_TEMPLATE_NAME, model);
        }
        return new PurchaseRedeemCodeResult(true, "购买成功", normalizedCardName, code.getRedeemCode(), amount,
                resolvedWalletTypeId, walletLogId);
    }

    @Override
    public int batchMarkSold(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        LocalDateTime now = LocalDateTime.now();
        return baseMapper.update(null, new LambdaUpdateWrapper<AiMemberCardRedeemCode>()
                .in(AiMemberCardRedeemCode::getId, ids)
                .eq(AiMemberCardRedeemCode::getStatus, 0)
                .eq(AiMemberCardRedeemCode::getSoldStatus, 0)
                .set(AiMemberCardRedeemCode::getSoldStatus, 1)
                .set(AiMemberCardRedeemCode::getUpdatedTime, now));
    }

    private String generateCode() {
        int len = 16;
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(CODE_CHARS[RANDOM.nextInt(CODE_CHARS.length)]);
        }
        return sb.toString();
    }

    private String generateCardNo() {
        return "MC" + IDGeneratorUtils.uuid32().substring(0, 16).toUpperCase();
    }
}
