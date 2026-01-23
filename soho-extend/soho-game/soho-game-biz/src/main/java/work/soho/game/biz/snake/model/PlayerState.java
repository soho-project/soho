package work.soho.game.biz.snake.model;

import lombok.Data;

/**
 * 玩家状态信息。
 */
@Data
public class PlayerState {
    /** 玩家 ID（登录用户用 userId 字符串） */
    private String playerId;
    /** 账号 UID */
    private String uid;
    /** 连接 ID */
    private String connectId;
    /** 昵称 */
    private String name;
    /** 是否准备 */
    private boolean ready;
    /** 是否存活 */
    private boolean alive;
    /** 当前积分 */
    private int score;
    /** 是否加速 */
    private boolean boosting;
    /** 当前蛇长度 */
    private int length;
    /** 磁铁效果截止时间戳 */
    private long magnetActiveUntil;
    /** 复活卡数量 */
    private int reviveCards;
}
