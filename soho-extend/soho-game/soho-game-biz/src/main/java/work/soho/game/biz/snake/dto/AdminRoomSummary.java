package work.soho.game.biz.snake.dto;

import lombok.Data;
import work.soho.game.biz.snake.model.GameRound;
import work.soho.game.biz.snake.model.GameRoomMode;

/**
 * 后台房间列表视图。
 */
@Data
public class AdminRoomSummary {
    private String roomId;
    private GameRoomMode mode;
    private int roundNo;
    private GameRound.Status status;
    private int playerCount;
    private int aliveCount;
    private long lastActiveAt;
}
