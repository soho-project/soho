package work.soho.game.biz.snake.dto;

import lombok.Data;
import work.soho.game.biz.snake.model.GameRound;
import work.soho.game.biz.snake.model.GameRoomMode;

import java.util.List;

/**
 * 房间快照视图。
 */
@Data
public class RoomSnapshot {
    /** 房间 ID */
    private String roomId;
    /** 房间模式 */
    private GameRoomMode mode;
    /** 局号 */
    private int roundNo;
    /** 局状态 */
    private GameRound.Status status;
    /** 玩家列表 */
    private List<PlayerView> players;
}
