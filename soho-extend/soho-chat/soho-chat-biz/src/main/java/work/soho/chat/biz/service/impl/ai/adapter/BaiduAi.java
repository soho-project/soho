package work.soho.chat.biz.service.impl.ai.adapter;

import okhttp3.*;
import work.soho.chat.api.request.AiRequest;
import work.soho.chat.api.service.ai.AiApiService;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

public class BaiduAi implements AiApiService {

    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    private BaiduAiOptions baiduAiOptions;

    public BaiduAi(BaiduAiOptions baiduAiOptions) {
        this.baiduAiOptions = baiduAiOptions;
    }

    /**
     * 获取Token
     *
     * @return
     * @throws IOException
     */
    private Map<String, String> getTokens() throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token?client_id="+baiduAiOptions.getApiKey()+"&client_secret="+baiduAiOptions.getSecretKey()+"&grant_type=client_credentials")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        System.out.println(response.body().string());
        return null;
    }

    public String query(String answer) {
        try {
            getTokens();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return answer;
    }

    @Override
    public String chatQuery(LinkedList<AiRequest.Message> list) {
        return null;
    }
}
