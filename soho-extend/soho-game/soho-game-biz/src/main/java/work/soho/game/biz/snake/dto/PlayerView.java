package work.soho.game.biz.snake.dto;

import lombok.Data;

/**
 * 玩家信息视图。
 */
@Data
public class PlayerView {
    /** 玩家 ID */
    private String playerId;
    /** 昵称 */
    private String name;
    /** 是否准备 */
    private boolean ready;
    /** 是否存活 */
    private boolean alive;
    /** 当前积分 */
    private int score;
    /** 当前长度 */
    private int length;
    /** 复活卡数量 */
    private int reviveCards;
}
