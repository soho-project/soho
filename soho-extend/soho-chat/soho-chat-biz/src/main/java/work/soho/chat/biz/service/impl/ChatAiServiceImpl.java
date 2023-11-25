package work.soho.chat.biz.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.soho.chat.biz.service.ChatAiService;
import work.soho.chat.biz.service.impl.ai.adapter.AliyunAi;
import work.soho.chat.biz.service.impl.ai.adapter.AliyunAiOptions;

@Log4j2
@Service
public class ChatAiServiceImpl implements ChatAiService {
    @Autowired
    private AliyunAiOptions aliyunAiOptions;

    @Override
    public String chat(String userInput) {
        AliyunAi aliyun = new AliyunAi(aliyunAiOptions);
        return aliyun.query(userInput);
    }
}
