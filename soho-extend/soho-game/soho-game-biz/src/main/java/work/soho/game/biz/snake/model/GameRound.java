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

    private int roundNo;
    private long tick;
    private int width;
    private int height;
    private long startedAt;
    private long durationMillis;
    private boolean resultSent;
    private Status status = Status.WAITING;
    private final Map<String, SnakeState> snakes = new HashMap<>();
    private final Set<FoodItem> foods = new HashSet<>();
}
