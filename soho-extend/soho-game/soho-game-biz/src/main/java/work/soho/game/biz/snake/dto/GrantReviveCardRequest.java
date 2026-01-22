package work.soho.game.biz.snake.dto;

import lombok.Data;

/**
 * 后台发放复活卡请求。
 */
@Data
public class GrantReviveCardRequest {
    private String roomId;
    private String playerId;
    private Integer count;
}
