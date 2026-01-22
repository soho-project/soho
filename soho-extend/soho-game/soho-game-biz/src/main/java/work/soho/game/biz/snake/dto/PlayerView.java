package work.soho.game.biz.snake.dto;

import lombok.Data;

/**
 * 玩家信息视图。
 */
@Data
public class PlayerView {
    private String playerId;
    private String name;
    private boolean ready;
    private boolean alive;
    private int score;
    private int length;
    private int reviveCards;
}
