package work.soho.game.biz.wordmatch.service;

import work.soho.game.biz.wordmatch.domain.WordMatchBattle;
import work.soho.game.biz.wordmatch.model.WordMatchRoomMode;

import java.time.LocalDateTime;
import java.util.Map;

public interface WordMatchBattleService {
    WordMatchBattle createBattle(String roomId, WordMatchRoomMode mode);

    WordMatchBattle getByRoomId(String roomId);

    void updateRunning(Long battleId, LocalDateTime startedAt);

    void updateFinished(Long battleId, String status, String winnerId, String endReason, Map<String, Integer> scores, LocalDateTime endedAt);
}
