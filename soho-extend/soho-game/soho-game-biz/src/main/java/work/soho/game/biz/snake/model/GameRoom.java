package work.soho.game.biz.snake.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 房间状态，包含玩家与当前局。
 */
@Data
public class GameRoom {
    private final Object lock = new Object();
    private String roomId;
    private GameRoomMode mode = GameRoomMode.ENDLESS;
    private int maxPlayers;
    private String ownerPlayerId;
    private int roundNo;
    private GameRound round;
    private final Map<String, PlayerState> players = new HashMap<>();
    private long createdAt;
    private long lastActiveAt;
}
