package work.soho.game.biz.wordmatch.model;

import lombok.Data;

import java.time.Instant;

/**
 * 房间内玩家信息。
 */
@Data
public class WordMatchRoomPlayer {
    private String playerId;
    private String name;
    private String uid;
    private String connectId;
    /** 玩家选用的词库等级（可选，如 CET4/CET6） */
    private String wordLevel;
    private WordMatchPlayerState state = WordMatchPlayerState.IDLE;
    private Instant joinedAt = Instant.now();
}
