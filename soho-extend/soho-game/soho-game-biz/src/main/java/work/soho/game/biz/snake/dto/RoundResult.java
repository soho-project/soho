package work.soho.game.biz.snake.dto;

import lombok.Data;
import work.soho.game.biz.snake.model.GameRoomMode;

import java.util.List;

/**
 * 对战结算结果。
 */
@Data
public class RoundResult {
    private String roomId;
    private int roundNo;
    private GameRoomMode mode;
    private List<ResultItem> rankings;

    @Data
    public static class ResultItem {
        private String playerId;
        private String name;
        private int score;
        private int length;
        private boolean alive;
        private int rank;
    }
}
