package work.soho.game.biz.wordmatch.dto;

import lombok.Data;

import java.util.Map;

/**
 * 结算结果。
 */
@Data
public class GameOverResult {
    private String roomId;
    private String winnerId;
    private String reason;
    private Map<String, Integer> scores;
}
