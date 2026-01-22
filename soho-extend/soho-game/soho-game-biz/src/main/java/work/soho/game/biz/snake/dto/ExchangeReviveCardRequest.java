package work.soho.game.biz.snake.dto;

import lombok.Data;

/**
 * 复活卡兑换请求。
 */
@Data
public class ExchangeReviveCardRequest {
    private String roomId;
    private String playerId;
    private Integer count;
}
