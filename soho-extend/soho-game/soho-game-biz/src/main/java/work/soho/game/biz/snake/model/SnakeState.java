package work.soho.game.biz.snake.model;

import lombok.Data;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 蛇状态，包含身体与移动方向。
 */
@Data
public class SnakeState {
    private Deque<Point> body = new ArrayDeque<>();
    private Direction direction = Direction.RIGHT;
    private Direction nextDirection = Direction.RIGHT;
}
