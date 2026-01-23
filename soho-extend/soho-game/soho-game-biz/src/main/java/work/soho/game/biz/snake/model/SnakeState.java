package work.soho.game.biz.snake.model;

import lombok.Data;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 蛇状态，包含身体与移动方向。
 */
@Data
public class SnakeState {
    /** 蛇身坐标（头在首位） */
    private Deque<Point> body = new ArrayDeque<>();
    /** 当前移动方向 */
    private Direction direction = Direction.RIGHT;
    /** 下一帧方向 */
    private Direction nextDirection = Direction.RIGHT;
}
