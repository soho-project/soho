package work.soho.game.biz.snake.model;

import lombok.Data;

/**
 * 玩家状态信息。
 */
@Data
public class PlayerState {
    private String playerId;
    private String uid;
    private String connectId;
    private String name;
    private boolean ready;
    private boolean alive;
    private int score;
    private boolean boosting;
    private int length;
}
