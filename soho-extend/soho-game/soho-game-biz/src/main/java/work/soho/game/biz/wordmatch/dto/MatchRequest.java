package work.soho.game.biz.wordmatch.dto;

import lombok.Data;

/**
 * 匹配请求。
 */
@Data
public class MatchRequest {
    private String mode;
    private String name;
    private String playerId;
    private String wordLevel;
    /**
     * 排位分（可选，仅用于 RANKED 匹配）。
     */
    private Integer rankScore;
}
