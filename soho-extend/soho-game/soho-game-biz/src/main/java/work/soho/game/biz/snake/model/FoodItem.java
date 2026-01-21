package work.soho.game.biz.snake.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * 食物实例。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"x", "y"})
public class FoodItem {
    private int x;
    private int y;
    private FoodType type;
    private int score;
}
