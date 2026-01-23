package work.soho.game.biz.snake.dto;

import lombok.Data;

/**
 * 后台发放复活卡请求。
 */
@Data
public class GrantReviveCardRequest {
    /** 房间 ID */
    private String roomId;
    /** 玩家 ID */
    private String playerId;
    /** 发放数量 */
    private Integer count;
}
