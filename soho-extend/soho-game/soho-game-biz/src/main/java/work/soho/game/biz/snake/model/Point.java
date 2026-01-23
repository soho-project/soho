package work.soho.game.biz.snake.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 坐标点 (格子坐标)。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Point {
    /** X 坐标 */
    private int x;
    /** Y 坐标 */
    private int y;
}
