package work.soho.game.biz.wordmatch.dto;

import lombok.Data;

/**
 * 游戏配置（V1.2 可扩展）。
 */
@Data
public class WordMatchConfig {
    private int boardWidth = 8;
    private int boardHeight = 8;
    private int duelMaxPlayers = 2;
    private int fourMaxPlayers = 4;
    private int rankedMaxPlayers = 2;
    private int aiMaxPlayers = 2;
    private int matchTimeoutSeconds = 30;
    private int baseRankScore = 1000;
    private int eloK = 32;
}
