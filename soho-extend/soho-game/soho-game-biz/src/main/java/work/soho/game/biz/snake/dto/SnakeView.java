package work.soho.game.biz.snake.dto;

import lombok.Data;
import work.soho.game.biz.snake.model.Point;

import java.util.List;

/**
 * 蛇状态视图。
 */
@Data
public class SnakeView {
    private String playerId;
    private boolean alive;
    private boolean boosting;
    private Long magnetUntil;
    private List<Point> body;
}
