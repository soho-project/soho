package work.soho.chat.biz.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.soho.chat.api.service.ai.AiApiService;
import work.soho.chat.biz.service.ChatAiService;
import work.soho.chat.biz.service.impl.ai.adapter.AliyunAi;
import work.soho.chat.biz.service.impl.ai.adapter.AliyunAiOptions;

@Log4j2
@Service
@RequiredArgsConstructor
public class ChatAiServiceImpl implements ChatAiService {

    private final AiApiService aiApiService;

    @Override
    public String chat(String userInput) {
        return aiApiService.query(userInput);
    }
}
