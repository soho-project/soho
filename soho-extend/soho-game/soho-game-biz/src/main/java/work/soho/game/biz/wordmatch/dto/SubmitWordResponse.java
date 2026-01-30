package work.soho.game.biz.wordmatch.dto;

import lombok.Data;

import java.util.Map;

/**
 * 提交单词响应。
 */
@Data
public class SubmitWordResponse {
    private boolean accepted;
    private int scoreDelta;
    private int combo;
    private String message;
    private Map<String, Object> boardDelta;
}
