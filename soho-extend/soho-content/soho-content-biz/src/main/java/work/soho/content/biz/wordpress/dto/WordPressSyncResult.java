package work.soho.content.biz.wordpress.dto;

import lombok.Data;

/**
 * WordPress 导入结果 DTO
 */
@Data
public class WordPressSyncResult {
    private int categoriesImported;
    private int tagsImported;
    private int usersImported;
    private int mediaImported;
    private int postsImported;
    private int pagesImported;
    private int commentsImported;
}
