package work.soho.game.biz.wordmatch.dto;

import lombok.Data;

/**
 * 技能释放请求。
 */
@Data
public class SkillCastRequest {
    private String roomId;
    private String playerId;
    private String skillCode;
}
