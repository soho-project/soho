package work.soho.game.biz.snake.dto;

import lombok.Data;
import work.soho.game.biz.snake.model.FoodItem;
import work.soho.game.biz.snake.model.GameRound;

import java.util.List;

/**
 * 单局状态视图。
 */
@Data
public class RoundState {
    /** 房间 ID */
    private String roomId;
    /** 局号 */
    private int roundNo;
    /** 当前 tick */
    private long tick;
    /** 地图宽度 */
    private int width;
    /** 地图高度 */
    private int height;
    /** 局状态 */
    private GameRound.Status status;
    /** 食物列表 */
    private List<FoodItem> foods;
    /** 蛇列表 */
    private List<SnakeView> snakes;
}
