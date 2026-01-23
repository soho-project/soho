package work.soho.game.biz.snake.dto;

import lombok.Data;

/**
 * 复活请求。
 */
@Data
public class ReviveRequest {
    /** 房间 ID */
    private String roomId;
    /** 玩家 ID */
    private String playerId;
}
