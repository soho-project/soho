package work.soho.game.biz.wordmatch.model;

import lombok.Data;
import work.soho.game.biz.wordmatch.board.WordMatchBoard;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 房间信息（简化版）。
 */
@Data
public class WordMatchRoom {
    private String roomId;
    private Long battleId;
    private WordMatchRoomMode mode = WordMatchRoomMode.DUEL;
    private int maxPlayers = 2;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
    private List<WordMatchRoomPlayer> players = new ArrayList<>();
    private WordMatchRound round = new WordMatchRound();
    private long eventSeq = 0L;
    private Map<String, Integer> comboMap = new HashMap<>();
    private Map<String, WordMatchBoard> boardMap = new HashMap<>();
}
