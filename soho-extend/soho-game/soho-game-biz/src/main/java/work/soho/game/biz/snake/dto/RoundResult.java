package work.soho.game.biz.snake.dto;

import lombok.Data;
import work.soho.game.biz.snake.model.GameRoomMode;

import java.util.List;

/**
 * 对战结算结果。
 */
@Data
public class RoundResult {
    /** 房间 ID */
    private String roomId;
    /** 局号 */
    private int roundNo;
    /** 房间模式 */
    private GameRoomMode mode;
    /** 排名列表 */
    private List<ResultItem> rankings;

    @Data
    public static class ResultItem {
        /** 玩家 ID */
        private String playerId;
        /** 昵称 */
        private String name;
        /** 积分 */
        private int score;
        /** 长度 */
        private int length;
        /** 是否存活 */
        private boolean alive;
        /** 名次 */
        private int rank;
    }
}
