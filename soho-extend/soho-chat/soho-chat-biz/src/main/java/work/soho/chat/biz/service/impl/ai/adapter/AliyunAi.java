package work.soho.chat.biz.service.impl.ai.adapter;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.utils.Constants;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.tokenizers.Tokenization;
import com.alibaba.dashscope.tokenizers.TokenizationResult;
import work.soho.chat.api.request.AiRequest;
import work.soho.chat.api.service.ai.AiApiService;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * api key: sk-6b01bbd0351c45d8a662ba25145fdb9e
 *
 * @link https://help.aliyun.com/zh/dashscope/developer-reference/tongyi-qianwen-7b-14b-api-detailes?disableWebsiteRedirect=true
 *
 */
public class AliyunAi implements AiApiService {
    private AliyunAiOptions aliyunAiOptions;

    public AliyunAi(AliyunAiOptions options) {
        Constants.apiKey=options.getApiKey();
        aliyunAiOptions = options;
    }

    public void tokenizer() throws ApiException, NoApiKeyException, InputRequiredException {
        Constants.apiKey= aliyunAiOptions.getApiKey();
        Tokenization tokenizer = new Tokenization("http");
        MessageManager messageManager = new MessageManager(10);
        messageManager.add(Message.builder().role(Role.USER.getValue()).content("你好？").build());
        QwenParam param = QwenParam.builder()
                .model(Tokenization.Models.QWEN_PLUS)
                .messages(messageManager.get())
                .build();
        TokenizationResult result = tokenizer.call(param);
        System.out.println(result);
    }

    @Override
    public String query(String question) {
        try {
//            tokenizer();
            return callWithMessage(question);
        } catch (Exception e) {
            //ignore
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String chatQuery(LinkedList<AiRequest.Message> list) {
        try {
            LinkedList<Message> requestList = new LinkedList<>();
            Iterator<AiRequest.Message> iterator = list.iterator();
            while(iterator.hasNext()) {
                AiRequest.Message message = iterator.next();
                if(AiRequest.Role.USER.getName().equals(message.getRole())) {
                    requestList.add(Message.builder().role(Role.USER.getValue()).content(message.getContent()).build());
                } else if(AiRequest.Role.SYSTEM.getName().equals(message.getRole())) {
                    requestList.add(Message.builder().role(Role.SYSTEM.getValue()).content(message.getContent()).build());
                } else if(AiRequest.Role.ASSISTANT.getName().equals(message.getRole())) {
                    requestList.add(Message.builder().role(Role.ASSISTANT.getValue()).content(message.getContent()).build());
                }
            }

            Constants.apiKey=aliyunAiOptions.getApiKey();
            Generation gen = new Generation();

            QwenParam param =
                    QwenParam.builder().model("qwen-14b-chat")
                            .messages(requestList)
                            .resultFormat(QwenParam.ResultFormat.MESSAGE)
                            .topP(0.8)
                            .enableSearch(true)
                            .build();
            GenerationResult result = gen.call(param);
            System.out.println(result);
            return result.getOutput().getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String callWithMessage(String question)
            throws NoApiKeyException, ApiException, InputRequiredException {
        Constants.apiKey=aliyunAiOptions.getApiKey();
        Generation gen = new Generation();
        Message userMsg = Message.builder().role(Role.USER.getValue()).content(question).build();
        QwenParam param =
                QwenParam.builder().model("qwen-14b-chat")
                        .messages(Arrays.asList(userMsg))
                        .resultFormat(QwenParam.ResultFormat.MESSAGE)
                        .topP(0.8)
                        .enableSearch(true)
                        .build();
        GenerationResult result = gen.call(param);
        System.out.println(result);
        return result.getOutput().getChoices().get(0).getMessage().getContent();
    }
}
