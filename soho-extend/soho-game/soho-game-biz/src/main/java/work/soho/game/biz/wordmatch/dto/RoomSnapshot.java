package work.soho.game.biz.wordmatch.dto;

import lombok.Data;
import work.soho.game.biz.wordmatch.model.WordMatchRoomMode;
import work.soho.game.biz.wordmatch.model.WordMatchRoundStatus;

import java.util.List;
import java.util.Map;

/**
 * 房间快照。
 */
@Data
public class RoomSnapshot {
    private String roomId;
    private WordMatchRoomMode mode;
    private WordMatchRoundStatus status;
    private int maxPlayers;
    private List<String> players;
    private Map<String, Integer> scores;
}
