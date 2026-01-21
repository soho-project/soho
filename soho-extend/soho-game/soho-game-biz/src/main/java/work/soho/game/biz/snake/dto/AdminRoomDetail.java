package work.soho.game.biz.snake.dto;

import lombok.Data;

/**
 * 后台房间详情视图。
 */
@Data
public class AdminRoomDetail {
    private RoomSnapshot snapshot;
    private RoundState round;
}
