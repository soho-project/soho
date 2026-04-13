package work.soho.ai.biz.service;

import reactor.core.publisher.Flux;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import work.soho.ai.biz.request.OpenAiChatCompletionRequest;
import work.soho.ai.biz.request.OpenAiResponsesRequest;

import java.util.Map;

public interface AiOpenApiService {
    /**
     * 查询 OpenAI/Codex 兼容余额。
     *
     * @param authorization Bearer Token
     * @return 余额与用量信息
     */
    Map<String, Object> balance(String authorization);

    /**
     * 查询当前用户套餐用量信息。
     *
     * @param userId 当前登录用户 ID
     * @param newApiUserHeader 客户端透传用户 ID
     * @return 套餐用量信息
     */
    Map<String, Object> selfPackage(Long userId, String newApiUserHeader);

    /**
     * 查询模型列表。
     *
     * @param authorization Bearer Token
     * @return 模型数据
     */
    Map<String, Object> models(String authorization);

    /**
     * 查询 Gemini 原生模型列表。
     *
     * @param authorization Bearer Token
     * @return Gemini 模型数据
     */
    Map<String, Object> geminiModels(String authorization);

    /**
     * 按登录用户查询 Gemini 原生模型列表。
     *
     * @param userId 登录用户ID
     * @return Gemini 模型数据
     */
    Map<String, Object> geminiModelsByUserId(Long userId);

    /**
     * 发起 Gemini 原生 generateContent 请求。
     *
     * @param key 平台 API Key（query 参数）
     * @param model Gemini 模型名
     * @param request 请求体
     * @return Gemini 原生响应
     */
    Map<String, Object> geminiGenerateContent(String key, String model, Map<String, Object> request);

    /**
     * 按登录用户发起 Gemini 原生 generateContent 请求。
     *
     * @param userId 登录用户ID
     * @param model Gemini 模型名
     * @param request 请求体
     * @return Gemini 原生响应
     */
    Map<String, Object> geminiGenerateContentByUserId(Long userId, String model, Map<String, Object> request);

    /**
     * 发起 chat completions 请求。
     *
     * @param authorization Bearer Token
     * @param request 请求体
     * @return OpenAI 兼容响应
     */
    Map<String, Object> chatCompletions(String authorization, OpenAiChatCompletionRequest request);

    /**
     * 发起流式 chat completions 请求。
     *
     * @param authorization Bearer Token
     * @param request 请求体
     * @return SSE 数据流
     */
    Flux<String> streamChatCompletions(String authorization, OpenAiChatCompletionRequest request);

    /**
     * 发起 responses 请求。
     *
     * @param authorization Bearer Token
     * @param request 请求体
     * @return OpenAI responses 兼容响应
     */
    Map<String, Object> responses(String authorization, OpenAiResponsesRequest request);

    /**
     * 发起流式 responses 请求。
     *
     * @param authorization Bearer Token
     * @param request 请求体
     * @return SSE 数据流
     */
    Flux<String> streamResponses(String authorization, OpenAiResponsesRequest request);

    /**
     * 发起 images generations 请求。
     *
     * @param authorization Bearer Token
     * @param request 请求体
     * @return OpenAI 兼容响应
     */
    Map<String, Object> imageGenerations(String authorization, Map<String, Object> request);

    /**
     * 发起 embeddings 请求。
     *
     * @param authorization Bearer Token
     * @param request 请求体
     * @return OpenAI 兼容响应
     */
    Map<String, Object> embeddings(String authorization, Map<String, Object> request);

    /**
     * 发起 audio transcriptions 请求。
     *
     * @param authorization Bearer Token
     * @param request 请求参数
     * @param file 音频文件
     * @return OpenAI 兼容响应
     */
    Object audioTranscriptions(String authorization, Map<String, String> request, MultipartFile file);

    /**
     * 发起 audio translations 请求。
     *
     * @param authorization Bearer Token
     * @param request 请求参数
     * @param file 音频文件
     * @return OpenAI 兼容响应
     */
    Object audioTranslations(String authorization, Map<String, String> request, MultipartFile file);

    /**
     * 发起 audio speech 请求。
     *
     * @param authorization Bearer Token
     * @param request 请求体
     * @return 音频二进制响应
     */
    ResponseEntity<byte[]> audioSpeech(String authorization, Map<String, Object> request);
}
