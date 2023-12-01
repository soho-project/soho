package work.soho.chat.biz.service.impl.ai.adapter;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.log4j.Log4j2;
import work.soho.chat.api.service.ai.AiApiService;
import work.soho.chat.biz.service.impl.ai.adapter.sohoai.CompletionResult;
import work.soho.common.core.util.JacksonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对接的事开源模型， 用阿里云开源模型为例
 *
 * https://github.com/QwenLM/Qwen.git
 *
 */
@Log4j2
public class SohoAi implements AiApiService {
    private SohoAiOptions sohoAiOptions;

    public SohoAi(SohoAiOptions sohoAiOptions) {
        this.sohoAiOptions = sohoAiOptions;
    }

    @Override
    public String query(String question) {
        List<Map<String,String>> list = new ArrayList<>();
        Map<String, String> msg1 = new HashMap<>();
        msg1.put("content", question);
        msg1.put("role", "user");
        list.add(msg1);

        Map<String, Object> params = new HashMap<>();
        params.put("model", sohoAiOptions.getModel());
        params.put("temperature", sohoAiOptions.getTemperature());  // 0-2 之间   越大多样性创造性越高
        params.put("top_p", sohoAiOptions.getTopP()); //用于约束生成词分部 0 - 1
        params.put("max_length", sohoAiOptions.getMaxLength()); //生成最大长度
        params.put("stream", sohoAiOptions.getStream());
        params.put("messages", list);

        String body = HttpUtil.post(sohoAiOptions.getApiPrefix() + "/v1/chat/completions", JacksonUtils.toJson(params));
        log.info(body);
        CompletionResult data = JSONUtil.toBean(body, CompletionResult.class);
        return data.getChoices().get(0).getMessage().getContent();
    }
}
