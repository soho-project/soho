package work.soho.content.biz.wordpress.dto;

/**
 * WordPress WXR 导入结果
 */
public class WordPressWxrImportResult {
    /**
     * 导入文章数量
     */
    private int posts;
    /**
     * 导入页面数量
     */
    private int pages;
    /**
     * 导入媒体数量
     */
    private int media;
    /**
     * 导入评论数量
     */
    private int comments;

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getMedia() {
        return media;
    }

    public void setMedia(int media) {
        this.media = media;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public void incrementPosts() {
        this.posts++;
    }

    public void incrementPages() {
        this.pages++;
    }

    public void incrementMedia() {
        this.media++;
    }

    public void incrementComments() {
        this.comments++;
    }
}
