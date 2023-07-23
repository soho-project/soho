package work.soho.chat.biz.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.client.OpenAiApi;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.ImageResult;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Retrofit;
import work.soho.chat.biz.service.ChatAiService;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.theokanning.openai.service.OpenAiService.*;

@Log4j2
@Service
public class ChatAiServiceImpl implements ChatAiService {
    @Value("${chat.ai.proxy.hostname}")
    private String proxyHostname;

    @Value("${chat.ai.proxy.port}")
    private int proxyPort;

    @Value("${chat.ai.proxy.username}")
    private String proxyUsername;

    @Value("${chat.ai.proxy.password}")
    private String proxyPassword;

    @Value("${chat.ai.account.chatAiEndpoint}")
    private String chatAiEndpoint;

    @Value("${chat.ai.account.chatAiToken}")
    private String chatAiKey;

    private String deploymentOrModelId = "gpt-3.5-turbo-16k";

    @Override
    public String chat(String userInput) {
        //问答方式
//        completion(userInput);
        //聊天方式获取
        return chatCompletion(userInput);
//        return null;
    }

    @Override
    public String createImage(String userInput) {
        OpenAiService openAi = getService();
        CreateImageRequest createImageRequest = new CreateImageRequest();
        createImageRequest.setN(1);
        createImageRequest.setPrompt(userInput);
//        createImageRequest.setSize("1024X1024");
        ImageResult imageResult = openAi.createImage(createImageRequest);
        System.out.println(imageResult);
        return imageResult.getData().get(0).getUrl();
    }

    private String completion(String text) {
        CompletionRequest completionRequest = CompletionRequest.builder()
//                .model("ada")
                .model("babbage")
                .prompt(text)
                .echo(true)
                .user(ChatMessageRole.USER.value())
                .n(3)
                .build();
        AtomicReference<String> responseText =  new AtomicReference<>("");
        getService().createCompletion(completionRequest).getChoices().forEach(item -> {
            responseText.set(responseText.get() + item.getText());
        });
        return responseText.get();
    }

    private String chatCompletion(String ask) {
        log.info("ask chatgpt: {}", ask);

        final List<ChatMessage> messages = new ArrayList<>();
//        messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), "你是电商客服;" +
//                "如果用户问快递你就回复: queryExpressDelivery([订单号])" +
//                "如果用户问订单状态就回复: queryOrderStatus([订单号])" +
//                "如果用户询问产品信息回复: queryGoods([商品名])"));
//
        messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), "你是Java程序员，根据用户提供的要求输出代码。如果是要求创建图片请输出 createImage([图片描述])"));

        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.USER.value(), ask);
        messages.add(systemMessage);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model(deploymentOrModelId)
                .messages(messages)
                .n(1)
                .maxTokens(16000)   // 4096  16384 16410
                .logitBias(new HashMap<>())
                .build();
        OpenAiService service = getService();
        AtomicReference<String> responseText =  new AtomicReference<>("");
        service.streamChatCompletion(chatCompletionRequest)
                .doOnError(Throwable::printStackTrace)
                .blockingForEach(message -> {
//                    System.out.println(message);
                    message.getChoices().forEach(choice ->{
                        String str = responseText.get();
                        if(choice.getMessage().getContent() != null) {
                            responseText.set(str + choice.getMessage().getContent());
                        }
                    });
                });
        log.info("chatgpt response: {}", responseText.get());
//        service.shutdownExecutor();
        //对结果进行处理
        if(responseText.get().startsWith("createImage(")) {
            log.info("开始创建图片： {}", responseText.get());
            return createImage(extractImageText(responseText.get()));
        }

        return responseText.get();
    }

    private OpenAiService getService() {
        ObjectMapper mapper = defaultObjectMapper();
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHostname, proxyPort));
        OkHttpClient client = defaultClient(chatAiKey, Duration.ofMinutes(100))
                .newBuilder()
                .proxy(proxy)
                .build();
        Retrofit retrofit = defaultRetrofit(client, mapper);
        OpenAiApi api = retrofit.create(OpenAiApi.class);

        OpenAiService service = new OpenAiService(api);
        return service;
    }

    /**
     * 提取创建图片的要求内容
     *
     * @param input
     * @return
     */
    public static String extractImageText(String input) {
        // 定义正则表达式，使用括号捕获变化的部分
        String regex = "createImage(.*)";
        // 创建Pattern对象
        Pattern pattern = Pattern.compile(regex);
        // 创建Matcher对象
        Matcher matcher = pattern.matcher(input);

        // 查找匹配的文字
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }
}
