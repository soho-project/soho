package work.soho.content.biz.controller.wp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.soho.content.biz.wordpress.WordPressQueryService;
import work.soho.content.biz.wordpress.dto.WordPressCommentDto;
import work.soho.content.biz.wordpress.dto.WordPressMediaDto;
import work.soho.content.biz.wordpress.dto.WordPressPostDto;
import work.soho.content.biz.wordpress.dto.WordPressTermDto;
import work.soho.content.biz.wordpress.dto.WordPressUserDto;

import java.util.List;

/**
 * WordPress 兼容 REST 接口
 */
@RestController
@RequestMapping("/wp-json/wp/v2")
public class WordPressCompatController {
    private final WordPressQueryService queryService;

    public WordPressCompatController(WordPressQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/posts")
    public List<WordPressPostDto> posts(@RequestParam(name = "page", defaultValue = "1") int page,
                                        @RequestParam(name = "per_page", defaultValue = "10") int perPage) {
        return queryService.listPosts("post", page, perPage);
    }

    @GetMapping("/pages")
    public List<WordPressPostDto> pages(@RequestParam(name = "page", defaultValue = "1") int page,
                                        @RequestParam(name = "per_page", defaultValue = "10") int perPage) {
        return queryService.listPosts("page", page, perPage);
    }

    @GetMapping("/categories")
    public List<WordPressTermDto> categories(@RequestParam(name = "page", defaultValue = "1") int page,
                                             @RequestParam(name = "per_page", defaultValue = "10") int perPage) {
        return queryService.listCategories(page, perPage);
    }

    @GetMapping("/tags")
    public List<WordPressTermDto> tags(@RequestParam(name = "page", defaultValue = "1") int page,
                                       @RequestParam(name = "per_page", defaultValue = "10") int perPage) {
        return queryService.listTags(page, perPage);
    }

    @GetMapping("/media")
    public List<WordPressMediaDto> media(@RequestParam(name = "page", defaultValue = "1") int page,
                                         @RequestParam(name = "per_page", defaultValue = "10") int perPage) {
        return queryService.listMedia(page, perPage);
    }

    @GetMapping("/users")
    public List<WordPressUserDto> users(@RequestParam(name = "page", defaultValue = "1") int page,
                                        @RequestParam(name = "per_page", defaultValue = "10") int perPage) {
        return queryService.listUsers(page, perPage);
    }

    @GetMapping("/comments")
    public List<WordPressCommentDto> comments(@RequestParam(name = "page", defaultValue = "1") int page,
                                              @RequestParam(name = "per_page", defaultValue = "10") int perPage) {
        return queryService.listComments(page, perPage);
    }
}
