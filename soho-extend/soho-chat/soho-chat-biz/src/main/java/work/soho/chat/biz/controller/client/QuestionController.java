package work.soho.chat.biz.controller.client;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.chat.api.service.QuestionService;
import work.soho.common.core.result.R;

@RestController
@RequestMapping("/client/api/question")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping
    public R get(String q) {
        return R.success(questionService.ask(null, q));
    }
}
