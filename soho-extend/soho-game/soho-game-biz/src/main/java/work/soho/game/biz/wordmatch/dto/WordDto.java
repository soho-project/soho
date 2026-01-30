package work.soho.game.biz.wordmatch.dto;

import lombok.Data;

/**
 * 词库条目。
 */
@Data
public class WordDto {
    private Long id;
    private String word;
    private String meaning;
    private String level;
    private Double freq;
}
