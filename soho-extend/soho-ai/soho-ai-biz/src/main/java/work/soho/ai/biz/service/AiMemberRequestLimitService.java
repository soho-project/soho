package work.soho.ai.biz.service;

import work.soho.ai.biz.dto.AiUsageSummary;

import java.util.Optional;

public interface AiMemberRequestLimitService {
    Decision evaluate(Long userId, Optional<AiUserMemberCardService.ActiveMemberCard> activeMemberCard);

    void consumeIfNeeded(Decision decision, String requestId, AiUsageSummary usage);

    UsageSnapshot queryUsage(Long userId, AiUserMemberCardService.ActiveMemberCard activeMemberCard);

    final class Decision {
        private final boolean memberByRequest;
        private final boolean overLimit;
        private final String limitMode;
        private final Long userId;
        private final Long cardId;
        private final Long nowMillis;
        private final int sevenDayWindowDays;
        private final int fiveHourWindowHours;
        private final boolean sevenDayEnabled;
        private final boolean fiveHourEnabled;

        public Decision(boolean memberByRequest, boolean overLimit, String limitMode, Long userId, Long cardId, Long nowMillis,
                        int sevenDayWindowDays, int fiveHourWindowHours,
                        boolean sevenDayEnabled, boolean fiveHourEnabled) {
            this.memberByRequest = memberByRequest;
            this.overLimit = overLimit;
            this.limitMode = limitMode;
            this.userId = userId;
            this.cardId = cardId;
            this.nowMillis = nowMillis;
            this.sevenDayWindowDays = sevenDayWindowDays;
            this.fiveHourWindowHours = fiveHourWindowHours;
            this.sevenDayEnabled = sevenDayEnabled;
            this.fiveHourEnabled = fiveHourEnabled;
        }

        public static Decision nonMember() {
            return new Decision(false, false, "", null, null, null, 0, 0, false, false);
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

        public boolean isByRequestMode() {
            return "by_request".equalsIgnoreCase(limitMode);
        }

        public boolean isByTokenMode() {
            return "by_token".equalsIgnoreCase(limitMode);
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

    final class UsageSnapshot {
        private final boolean usageAvailable;
        private final boolean sevenDayEnabled;
        private final boolean fiveHourEnabled;
        private final int sevenDayWindowDays;
        private final int fiveHourWindowHours;
        private final int sevenDayLimit;
        private final int fiveHourLimit;
        private final int sevenDayUsed;
        private final int fiveHourUsed;
        private final long sevenDayNextResetMillis;
        private final long fiveHourNextResetMillis;

        public UsageSnapshot(boolean usageAvailable, boolean sevenDayEnabled, boolean fiveHourEnabled,
                             int sevenDayWindowDays, int fiveHourWindowHours,
                             int sevenDayLimit, int fiveHourLimit,
                             int sevenDayUsed, int fiveHourUsed,
                             long sevenDayNextResetMillis, long fiveHourNextResetMillis) {
            this.usageAvailable = usageAvailable;
            this.sevenDayEnabled = sevenDayEnabled;
            this.fiveHourEnabled = fiveHourEnabled;
            this.sevenDayWindowDays = sevenDayWindowDays;
            this.fiveHourWindowHours = fiveHourWindowHours;
            this.sevenDayLimit = sevenDayLimit;
            this.fiveHourLimit = fiveHourLimit;
            this.sevenDayUsed = sevenDayUsed;
            this.fiveHourUsed = fiveHourUsed;
            this.sevenDayNextResetMillis = sevenDayNextResetMillis;
            this.fiveHourNextResetMillis = fiveHourNextResetMillis;
        }

        public static UsageSnapshot empty() {
            return new UsageSnapshot(false, false, false, 0, 0, 0, 0, 0, 0, 0L, 0L);
        }

        public boolean isUsageAvailable() {
            return usageAvailable;
        }

        public boolean isSevenDayEnabled() {
            return sevenDayEnabled;
        }

        public boolean isFiveHourEnabled() {
            return fiveHourEnabled;
        }

        public int getSevenDayWindowDays() {
            return sevenDayWindowDays;
        }

        public int getFiveHourWindowHours() {
            return fiveHourWindowHours;
        }

        public int getSevenDayLimit() {
            return sevenDayLimit;
        }

        public int getFiveHourLimit() {
            return fiveHourLimit;
        }

        public int getSevenDayUsed() {
            return sevenDayUsed;
        }

        public int getFiveHourUsed() {
            return fiveHourUsed;
        }

        public long getSevenDayNextResetMillis() {
            return sevenDayNextResetMillis;
        }

        public long getFiveHourNextResetMillis() {
            return fiveHourNextResetMillis;
        }

        public int getSevenDayRemaining() {
            return Math.max(sevenDayLimit - sevenDayUsed, 0);
        }

        public int getFiveHourRemaining() {
            return Math.max(fiveHourLimit - fiveHourUsed, 0);
        }

        public int getSevenDayProgress() {
            if (sevenDayLimit <= 0) {
                return 0;
            }
            return Math.min((int) ((long) sevenDayUsed * 100 / sevenDayLimit), 100);
        }

        public int getFiveHourProgress() {
            if (fiveHourLimit <= 0) {
                return 0;
            }
            return Math.min((int) ((long) fiveHourUsed * 100 / fiveHourLimit), 100);
        }
    }
}
