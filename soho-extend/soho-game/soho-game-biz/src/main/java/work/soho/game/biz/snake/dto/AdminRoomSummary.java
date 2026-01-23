package work.soho.game.biz.snake.dto;

import lombok.Data;
import work.soho.game.biz.snake.model.GameRound;
import work.soho.game.biz.snake.model.GameRoomMode;

/**
 * 后台房间列表视图。
 */
@Data
public class AdminRoomSummary {
    /** 房间 ID */
    private String roomId;
    /** 房间模式 */
    private GameRoomMode mode;
    /** 局号 */
    private int roundNo;
    /** 局状态 */
    private GameRound.Status status;
    /** 玩家数 */
    private int playerCount;
    /** 存活人数 */
    private int aliveCount;
    /** 最近活动时间 */
    private long lastActiveAt;
}
