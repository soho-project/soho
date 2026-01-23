package work.soho.game.biz.snake.dto;

import lombok.Data;

/**
 * 复活卡兑换请求。
 */
@Data
public class ExchangeReviveCardRequest {
    /** 房间 ID */
    private String roomId;
    /** 玩家 ID */
    private String playerId;
    /** 兑换数量 */
    private Integer count;
}
