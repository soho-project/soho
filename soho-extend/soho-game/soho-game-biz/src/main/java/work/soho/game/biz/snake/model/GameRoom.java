package work.soho.game.biz.snake.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 房间状态，包含玩家与当前局。
 */
@Data
public class GameRoom {
    /** 房间锁 */
    private final Object lock = new Object();
    /** 房间 ID */
    private String roomId;
    /** 房间模式 */
    private GameRoomMode mode = GameRoomMode.ENDLESS;
    /** 最大玩家数 */
    private int maxPlayers;
    /** 房主玩家 ID */
    private String ownerPlayerId;
    /** 当前局号 */
    private int roundNo;
    /** 当前局 */
    private GameRound round;
    /** 房间玩家集合 */
    private final Map<String, PlayerState> players = new HashMap<>();
    /** 创建时间戳 */
    private long createdAt;
    /** 最近活动时间戳 */
    private long lastActiveAt;
}
