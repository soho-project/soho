package work.soho.ai.biz.service;

import java.util.Optional;

public interface AiMemberRequestLimitService {
    Decision evaluate(Long userId, Optional<AiUserMemberCardService.ActiveMemberCard> activeMemberCard);

    void consumeIfNeeded(Decision decision, String requestId);

    final class Decision {
        private final boolean memberByRequest;
        private final boolean overLimit;
        private final Long userId;
        private final Long cardId;
        private final Long nowMillis;
        private final int sevenDayWindowDays;
        private final int fiveHourWindowHours;
        private final boolean sevenDayEnabled;
        private final boolean fiveHourEnabled;

        public Decision(boolean memberByRequest, boolean overLimit, Long userId, Long cardId, Long nowMillis,
                        int sevenDayWindowDays, int fiveHourWindowHours,
                        boolean sevenDayEnabled, boolean fiveHourEnabled) {
            this.memberByRequest = memberByRequest;
            this.overLimit = overLimit;
            this.userId = userId;
            this.cardId = cardId;
            this.nowMillis = nowMillis;
            this.sevenDayWindowDays = sevenDayWindowDays;
            this.fiveHourWindowHours = fiveHourWindowHours;
            this.sevenDayEnabled = sevenDayEnabled;
            this.fiveHourEnabled = fiveHourEnabled;
        }

        public static Decision nonMember() {
            return new Decision(false, false, null, null, null, 0, 0, false, false);
        }

        public boolean isMemberByRequest() {
            return memberByRequest;
        }

        public boolean isOverLimit() {
            return overLimit;
        }

        public boolean shouldChargeNonMemberRate() {
            return !memberByRequest || overLimit;
        }

        public boolean canConsumeQuota() {
            return memberByRequest && !overLimit;
        }

        public Long getUserId() {
            return userId;
        }

        public Long getCardId() {
            return cardId;
        }

        public Long getNowMillis() {
            return nowMillis;
        }

        public int getSevenDayWindowDays() {
            return sevenDayWindowDays;
        }

        public int getFiveHourWindowHours() {
            return fiveHourWindowHours;
        }

        public boolean isSevenDayEnabled() {
            return sevenDayEnabled;
        }

        public boolean isFiveHourEnabled() {
            return fiveHourEnabled;
        }
    }
}
