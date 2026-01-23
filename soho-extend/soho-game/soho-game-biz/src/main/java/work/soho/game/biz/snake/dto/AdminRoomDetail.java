package work.soho.game.biz.snake.dto;

import lombok.Data;

/**
 * 后台房间详情视图。
 */
@Data
public class AdminRoomDetail {
    /** 房间快照 */
    private RoomSnapshot snapshot;
    /** 当前局状态 */
    private RoundState round;
}
