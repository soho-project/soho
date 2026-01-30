package work.soho.game.biz.wordmatch.ws;

import lombok.Data;

/**
 * WebSocket 消息统一结构。
 */
@Data
public class WordMatchMessage {
    private WordMatchMessageType type;
    private String roomId;
    private String userId;
    private Object payload;
}
