package work.soho.content.biz.wordpress.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * WordPress 导出结果 DTO
 */
@Data
public class WordPressExportResult {
    private List<WordPressPostDto> posts = new ArrayList<>();
    private List<WordPressPostDto> pages = new ArrayList<>();
    private List<WordPressTermDto> categories = new ArrayList<>();
    private List<WordPressTermDto> tags = new ArrayList<>();
    private List<WordPressMediaDto> media = new ArrayList<>();
    private List<WordPressUserDto> users = new ArrayList<>();
    private List<WordPressCommentDto> comments = new ArrayList<>();
}
