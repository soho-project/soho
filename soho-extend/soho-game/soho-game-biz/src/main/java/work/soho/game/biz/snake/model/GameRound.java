package work.soho.game.biz.snake.model;

import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 单局游戏状态。
 */
@Data
public class GameRound {
    public enum Status {
        WAITING,
        RUNNING,
        FINISHED
    }

    /** 局号 */
    private int roundNo;
    /** 当前 tick */
    private long tick;
    /** 地图宽度 */
    private int width;
    /** 地图高度 */
    private int height;
    /** 开始时间戳 */
    private long startedAt;
    /** 单局时长（毫秒） */
    private long durationMillis;
    /** 结算是否已发送 */
    private boolean resultSent;
    /** 当前状态 */
    private Status status = Status.WAITING;
    /** 玩家蛇状态 */
    private final Map<String, SnakeState> snakes = new HashMap<>();
    /** 食物列表 */
    private final Set<FoodItem> foods = new HashSet<>();
}
