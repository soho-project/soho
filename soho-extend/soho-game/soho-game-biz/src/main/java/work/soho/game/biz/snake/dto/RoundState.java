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
    private String roomId;
    private int roundNo;
    private long tick;
    private int width;
    private int height;
    private GameRound.Status status;
    private List<FoodItem> foods;
    private List<SnakeView> snakes;
}
