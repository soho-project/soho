package work.soho.game.biz.wordmatch.dto;

import lombok.Data;

import java.util.Map;

/**
 * 干扰请求。
 */
@Data
public class AttackRequest {
    private String roomId;
    private String fromPlayerId;
    private String targetPlayerId;
    private String attackType;
    private Map<String, Object> payload;
}
