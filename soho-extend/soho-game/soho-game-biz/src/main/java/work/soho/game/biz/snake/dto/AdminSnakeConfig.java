package work.soho.game.biz.snake.dto;

import lombok.Data;

/**
 * 后台配置视图。
 */
@Data
public class AdminSnakeConfig {
    /** 加速倍数 */
    private Integer boostMultiplier;
    /** 食物占比上限 */
    private Double foodMaxRatio;
}
