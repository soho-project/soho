package work.soho.ai.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import work.soho.ai.biz.domain.AiMemberCard;
import work.soho.ai.biz.domain.AiUserMemberCard;
import work.soho.ai.biz.dto.AiUserMemberCardView;
import work.soho.ai.biz.mapper.AiUserMemberCardMapper;
import work.soho.ai.biz.service.AiMemberCardService;
import work.soho.ai.biz.service.AiUserMemberCardService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AiUserMemberCardServiceImpl extends ServiceImpl<AiUserMemberCardMapper, AiUserMemberCard>
        implements AiUserMemberCardService {

    private final AiMemberCardService aiMemberCardService;

    public AiUserMemberCardServiceImpl(AiMemberCardService aiMemberCardService) {
        this.aiMemberCardService = aiMemberCardService;
    }

    @Override
    public Optional<ActiveMemberCard> resolveActiveMemberCard(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }
        LocalDateTime now = LocalDateTime.now();
        List<AiUserMemberCard> userCards = list(new LambdaQueryWrapper<AiUserMemberCard>()
                .eq(AiUserMemberCard::getUserId, userId)
                .eq(AiUserMemberCard::getStatus, 1)
                .le(AiUserMemberCard::getStartTime, now)
                .ge(AiUserMemberCard::getEndTime, now)
                .orderByDesc(AiUserMemberCard::getIsSelected)
                .orderByDesc(AiUserMemberCard::getPriority)
                .orderByDesc(AiUserMemberCard::getEndTime)
                .orderByDesc(AiUserMemberCard::getId));
        if (userCards == null || userCards.isEmpty()) {
            return Optional.empty();
        }

        Set<Long> cardIds = userCards.stream()
                .map(AiUserMemberCard::getMemberCardId)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());
        if (cardIds.isEmpty()) {
            return Optional.empty();
        }

        List<AiMemberCard> cards = aiMemberCardService.list(new LambdaQueryWrapper<AiMemberCard>()
                .in(AiMemberCard::getId, cardIds)
                .eq(AiMemberCard::getStatus, 1));
        if (cards == null || cards.isEmpty()) {
            return Optional.empty();
        }
        java.util.Map<Long, AiMemberCard> cardMap = cards.stream()
                .collect(Collectors.toMap(AiMemberCard::getId, Function.identity(), (a, b) -> a));

        for (AiUserMemberCard userCard : userCards) {
            AiMemberCard card = cardMap.get(userCard.getMemberCardId());
            if (card == null) {
                continue;
            }
            return Optional.of(new ActiveMemberCard(
                    userCard.getId(),
                    card.getLimitMode(),
                    card.getRateLimit5h(),
                    card.getRateLimit7d(),
                    card.getRateLimit5hEnabled(),
                    card.getRateLimit7dEnabled(),
                    card.getRateLimitWindow5h(),
                    card.getRateLimitWindow7d()
            ));
        }
        return Optional.empty();
    }

    @Override
    public List<AiUserMemberCardView> listUserCards(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        List<AiUserMemberCard> userCards = list(new LambdaQueryWrapper<AiUserMemberCard>()
                .eq(AiUserMemberCard::getUserId, userId)
                .orderByDesc(AiUserMemberCard::getIsSelected)
                .orderByDesc(AiUserMemberCard::getPriority)
                .orderByDesc(AiUserMemberCard::getEndTime)
                .orderByDesc(AiUserMemberCard::getId));
        if (userCards == null || userCards.isEmpty()) {
            return new ArrayList<>();
        }
        java.util.Map<Long, AiMemberCard> cardMap = loadCardMap(userCards);
        List<AiUserMemberCardView> result = new ArrayList<>();
        for (AiUserMemberCard userCard : userCards) {
            AiMemberCard card = cardMap.get(userCard.getMemberCardId());
            if (card == null) {
                continue;
            }
            result.add(toView(userCard, card));
        }
        return result;
    }

    @Override
    public Optional<AiUserMemberCardView> currentUserCard(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }
        LocalDateTime now = LocalDateTime.now();
        List<AiUserMemberCard> userCards = list(new LambdaQueryWrapper<AiUserMemberCard>()
                .eq(AiUserMemberCard::getUserId, userId)
                .eq(AiUserMemberCard::getStatus, 1)
                .le(AiUserMemberCard::getStartTime, now)
                .ge(AiUserMemberCard::getEndTime, now)
                .orderByDesc(AiUserMemberCard::getIsSelected)
                .orderByDesc(AiUserMemberCard::getPriority)
                .orderByDesc(AiUserMemberCard::getEndTime)
                .orderByDesc(AiUserMemberCard::getId));
        if (userCards == null || userCards.isEmpty()) {
            return Optional.empty();
        }
        java.util.Map<Long, AiMemberCard> cardMap = loadCardMap(userCards);
        for (AiUserMemberCard userCard : userCards) {
            AiMemberCard card = cardMap.get(userCard.getMemberCardId());
            if (card == null) {
                continue;
            }
            return Optional.of(toView(userCard, card));
        }
        return Optional.empty();
    }

    @Override
    public boolean selectUserCard(Long userId, Long userCardId) {
        if (userId == null || userCardId == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        AiUserMemberCard target = getOne(new LambdaQueryWrapper<AiUserMemberCard>()
                .eq(AiUserMemberCard::getId, userCardId)
                .eq(AiUserMemberCard::getUserId, userId)
                .eq(AiUserMemberCard::getStatus, 1)
                .le(AiUserMemberCard::getStartTime, now)
                .ge(AiUserMemberCard::getEndTime, now)
                .last("limit 1"));
        if (target == null) {
            return false;
        }
        update(new LambdaUpdateWrapper<AiUserMemberCard>()
                .eq(AiUserMemberCard::getUserId, userId)
                .set(AiUserMemberCard::getIsSelected, false));
        target.setIsSelected(true);
        target.setUpdatedTime(now);
        return updateById(target);
    }

    private java.util.Map<Long, AiMemberCard> loadCardMap(List<AiUserMemberCard> userCards) {
        Set<Long> cardIds = userCards.stream()
                .map(AiUserMemberCard::getMemberCardId)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());
        if (cardIds.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        List<AiMemberCard> cards = aiMemberCardService.list(new LambdaQueryWrapper<AiMemberCard>()
                .in(AiMemberCard::getId, cardIds));
        if (cards == null || cards.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        return cards.stream().collect(Collectors.toMap(AiMemberCard::getId, Function.identity(), (a, b) -> a));
    }

    private AiUserMemberCardView toView(AiUserMemberCard userCard, AiMemberCard card) {
        AiUserMemberCardView view = new AiUserMemberCardView();
        view.setUserCardId(userCard.getId());
        view.setMemberCardId(card.getId());
        view.setName(card.getName());
        view.setCardType(card.getCardType());
        view.setLimitMode(card.getLimitMode());
        view.setStatus(userCard.getStatus());
        view.setPriority(userCard.getPriority());
        view.setIsSelected(Boolean.TRUE.equals(userCard.getIsSelected()));
        view.setStartTime(userCard.getStartTime());
        view.setEndTime(userCard.getEndTime());
        view.setRateLimit5h(card.getRateLimit5h());
        view.setRateLimit7d(card.getRateLimit7d());
        view.setRateLimit5hEnabled(card.getRateLimit5hEnabled());
        view.setRateLimit7dEnabled(card.getRateLimit7dEnabled());
        view.setRateLimitWindow5h(card.getRateLimitWindow5h());
        view.setRateLimitWindow7d(card.getRateLimitWindow7d());
        return view;
    }
}
