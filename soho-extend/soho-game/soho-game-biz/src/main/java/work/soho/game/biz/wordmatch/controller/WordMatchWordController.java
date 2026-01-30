package work.soho.game.biz.wordmatch.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.result.R;
import work.soho.game.biz.wordmatch.dto.WordDto;
import work.soho.game.biz.wordmatch.service.WordLibraryService;

import java.util.List;

@Api(tags = "单词消消消乐词库接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/game/guest/word-match/words")
public class WordMatchWordController {
    private final WordLibraryService wordLibraryService;

    @GetMapping("/list")
    public R<List<WordDto>> list(@RequestParam(required = false) String level,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "20") int size) {
        return R.success(wordLibraryService.listWords(level, page, size));
    }
}
