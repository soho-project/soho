package work.soho.game.biz.snake.dto;

import lombok.Data;
import work.soho.game.biz.snake.model.Point;

import java.util.List;

/**
 * 蛇状态视图。
 */
@Data
public class SnakeView {
    /** 玩家 ID */
    private String playerId;
    /** 是否存活 */
    private boolean alive;
    /** 是否加速 */
    private boolean boosting;
    /** 磁铁截止时间戳 */
    private Long magnetUntil;
    /** 蛇身坐标 */
    private List<Point> body;
}
