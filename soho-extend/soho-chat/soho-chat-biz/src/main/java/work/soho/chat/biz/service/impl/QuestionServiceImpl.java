package work.soho.chat.biz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.chat.api.service.QuestionService;
import work.soho.chat.biz.service.ChatAiService;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final ChatAiService chatAiService;

    @Override
    public String ask(String uid, String q) {
        return chatAiService.chat(q);
    }
}
