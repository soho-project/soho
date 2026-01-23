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
    /** X 坐标 */
    private int x;
    /** Y 坐标 */
    private int y;
    /** 食物类型 */
    private FoodType type;
    /** 食物积分 */
    private int score;
}
