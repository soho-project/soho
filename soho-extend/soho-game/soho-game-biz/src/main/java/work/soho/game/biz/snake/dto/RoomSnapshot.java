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
    private String roomId;
    private GameRoomMode mode;
    private int roundNo;
    private GameRound.Status status;
    private List<PlayerView> players;
}
