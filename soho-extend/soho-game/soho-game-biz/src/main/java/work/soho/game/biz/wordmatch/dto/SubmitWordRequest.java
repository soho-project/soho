package work.soho.game.biz.wordmatch.dto;

import lombok.Data;

/**
 * 提交单词请求。
 */
@Data
public class SubmitWordRequest {
    private String roomId;
    private String playerId;
    private String word;
    /**
     * 可选：限制词库等级
     */
    private String wordLevel;
}
