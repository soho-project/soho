package work.soho.ai.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.ai.biz.domain.AiMemberCardRedeemCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface AiMemberCardRedeemCodeService extends IService<AiMemberCardRedeemCode> {
    BatchGenerateResult batchGenerate(Long memberCardId, Integer count, String batchNo,
                                      LocalDateTime expireTime, String remark);

    RedeemResult redeem(Long userId, String redeemCode);

    PurchaseRedeemCodeResult purchaseByMemberCardName(Long userId, String memberCardName, String email);

    int batchMarkSold(List<Long> ids);

    final class BatchGenerateResult {
        private final String batchNo;
        private final int generatedCount;

        public BatchGenerateResult(String batchNo, int generatedCount) {
            this.batchNo = batchNo;
            this.generatedCount = generatedCount;
        }

        public String getBatchNo() {
            return batchNo;
        }

        public int getGeneratedCount() {
            return generatedCount;
        }
    }

    final class RedeemResult {
        private final boolean success;
        private final String message;

        public RedeemResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }

    final class PurchaseRedeemCodeResult {
        private final boolean success;
        private final String message;
        private final String memberCardName;
        private final String redeemCode;
        private final BigDecimal amount;
        private final Integer walletTypeId;
        private final Long walletLogId;

        public PurchaseRedeemCodeResult(boolean success, String message, String memberCardName,
                                        String redeemCode, BigDecimal amount, Integer walletTypeId,
                                        Long walletLogId) {
            this.success = success;
            this.message = message;
            this.memberCardName = memberCardName;
            this.redeemCode = redeemCode;
            this.amount = amount;
            this.walletTypeId = walletTypeId;
            this.walletLogId = walletLogId;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public String getMemberCardName() {
            return memberCardName;
        }

        public String getRedeemCode() {
            return redeemCode;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public Integer getWalletTypeId() {
            return walletTypeId;
        }

        public Long getWalletLogId() {
            return walletLogId;
        }
    }
}
