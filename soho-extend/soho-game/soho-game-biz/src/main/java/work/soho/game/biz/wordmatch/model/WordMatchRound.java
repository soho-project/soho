package work.soho.game.biz.wordmatch.model;

import lombok.Data;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * 单局对战状态快照（简化版）。
 */
@Data
public class WordMatchRound {
    private WordMatchRoundStatus status = WordMatchRoundStatus.WAITING;
    private Instant startedAt;
    private Instant endedAt;
    private Map<String, Integer> scores = new HashMap<>();
}
