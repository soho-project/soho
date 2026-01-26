package work.soho.content.biz.wordpress.dto;

import lombok.Data;

/**
 * WordPress 导入请求 DTO
 */
@Data
public class WordPressSyncRequest {
    private boolean posts = true;
    private boolean pages = true;
    private boolean categories = true;
    private boolean tags = true;
    private boolean media = true;
    private boolean users = true;
    private boolean comments = true;
    private int page = 1;
    private int perPage = 50;
    private boolean upsert = true;
}
