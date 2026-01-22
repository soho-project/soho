package work.soho.game.biz.snake.dto;

import lombok.Data;

/**
 * 复活请求。
 */
@Data
public class ReviveRequest {
    private String roomId;
    private String playerId;
}
