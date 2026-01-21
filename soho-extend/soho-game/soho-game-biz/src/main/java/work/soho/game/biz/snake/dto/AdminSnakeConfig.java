package work.soho.game.biz.snake.dto;

import lombok.Data;

/**
 * 后台配置视图。
 */
@Data
public class AdminSnakeConfig {
    private Integer boostMultiplier;
    private Double foodMaxRatio;
}
