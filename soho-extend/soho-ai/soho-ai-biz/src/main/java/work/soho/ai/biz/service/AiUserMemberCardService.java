package work.soho.ai.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.ai.biz.domain.AiUserMemberCard;
import work.soho.ai.biz.dto.AiUserMemberCardView;

import java.util.List;
import java.util.Optional;

public interface AiUserMemberCardService extends IService<AiUserMemberCard> {
    Optional<ActiveMemberCard> resolveActiveMemberCard(Long userId);

    List<AiUserMemberCardView> listUserCards(Long userId);

    Optional<AiUserMemberCardView> currentUserCard(Long userId);

    boolean selectUserCard(Long userId, Long userCardId);

    final class ActiveMemberCard {
        private final Long userCardId;
        private final String limitMode;
        private final Integer rateLimit5h;
        private final Integer rateLimit7d;
        private final Boolean rateLimit5hEnabled;
        private final Boolean rateLimit7dEnabled;
        private final Integer rateLimitWindow5h;
        private final Integer rateLimitWindow7d;

        public ActiveMemberCard(Long userCardId, String limitMode,
                                Integer rateLimit5h, Integer rateLimit7d,
                                Boolean rateLimit5hEnabled, Boolean rateLimit7dEnabled,
                                Integer rateLimitWindow5h, Integer rateLimitWindow7d) {
            this.userCardId = userCardId;
            this.limitMode = limitMode;
            this.rateLimit5h = rateLimit5h;
            this.rateLimit7d = rateLimit7d;
            this.rateLimit5hEnabled = rateLimit5hEnabled;
            this.rateLimit7dEnabled = rateLimit7dEnabled;
            this.rateLimitWindow5h = rateLimitWindow5h;
            this.rateLimitWindow7d = rateLimitWindow7d;
        }

        public Long getUserCardId() {
            return userCardId;
        }

        public String getLimitMode() {
            return limitMode;
        }

        public Integer getRateLimit5h() {
            return rateLimit5h;
        }

        public Integer getRateLimit7d() {
            return rateLimit7d;
        }

        public Boolean getRateLimit5hEnabled() {
            return rateLimit5hEnabled;
        }

        public Boolean getRateLimit7dEnabled() {
            return rateLimit7dEnabled;
        }

        public Integer getRateLimitWindow5h() {
            return rateLimitWindow5h;
        }

        public Integer getRateLimitWindow7d() {
            return rateLimitWindow7d;
        }
    }
}
