package work.soho.ai.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.ai.biz.domain.AiMemberCardRedeemCode;

import java.time.LocalDateTime;

public interface AiMemberCardRedeemCodeService extends IService<AiMemberCardRedeemCode> {
    BatchGenerateResult batchGenerate(Long memberCardId, Integer count, String batchNo,
                                      LocalDateTime expireTime, String remark);

    RedeemResult redeem(Long userId, String redeemCode);

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
}
