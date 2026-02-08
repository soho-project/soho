package work.soho.ai.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import work.soho.ai.biz.domain.AiApp;
import work.soho.ai.biz.domain.AiProviderConfig;
import work.soho.ai.biz.dto.AiChatResponse;
import work.soho.ai.biz.request.AiChatRequest;
import work.soho.ai.biz.service.AiAppService;
import work.soho.ai.biz.service.AiChatService;
import work.soho.ai.biz.service.AiProviderConfigService;
import work.soho.common.core.util.JacksonUtils;
import work.soho.common.core.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService {
    private static final int DEFAULT_TIMEOUT_MS = 60000;
    private final AiAppService aiAppService;
    private final AiProviderConfigService aiProviderConfigService;

    @Override
    public AiChatResponse chat(AiChatRequest request) {
        AiApp aiApp = resolveApp(request.getAppCode());
        AiProviderConfig providerConfig = resolveProviderConfig(aiApp, request.getProviderCode());

        Map<String, Object> config = parseConfig(providerConfig.getConfigJson());
        String provider = pickProvider(providerConfig, config);
        String apiKey = pickApiKey(providerConfig, config);
        String baseUrl = pickBaseUrl(providerConfig, config);
        String model = normalizeModel(provider, pickModel(request, aiApp, providerConfig, config));
        Integer timeoutMs = pickInteger(config, "timeoutMs", providerConfig.getTimeoutMs());

        List<AiChatRequest.Message> messages = buildMessages(request, aiApp);
        if (messages.isEmpty()) {
            throw new IllegalArgumentException("messages is empty");
        }
        validateRequired(provider, apiKey, baseUrl, model);

        switch (provider.toLowerCase(Locale.ROOT)) {
            case "anthropic":
                return callAnthropic(provider, baseUrl, apiKey, model, messages, request, aiApp, config, timeoutMs);
            case "gemini":
                return callGemini(provider, baseUrl, apiKey, model, messages, request, aiApp, config, timeoutMs);
            case "ollama":
                return callOllama(provider, baseUrl, model, messages, request, config, timeoutMs);
            case "openai":
            case "deepseek":
            case "qwen":
            default:
                return callOpenAiCompatible(provider, baseUrl, apiKey, model, messages, request, aiApp, config, timeoutMs);
        }
    }

    @Override
    public Flux<String> streamChat(AiChatRequest request) {
        AiApp aiApp = resolveApp(request.getAppCode());
        AiProviderConfig providerConfig = resolveProviderConfig(aiApp, request.getProviderCode());

        Map<String, Object> config = parseConfig(providerConfig.getConfigJson());
        String provider = pickProvider(providerConfig, config);
        String apiKey = pickApiKey(providerConfig, config);
        String baseUrl = pickBaseUrl(providerConfig, config);
        String model = normalizeModel(provider, pickModel(request, aiApp, providerConfig, config));
        Boolean streamSupported = pickBoolean(config, "streamSupported", true);

        List<AiChatRequest.Message> messages = buildMessages(request, aiApp);
        if (messages.isEmpty()) {
            return Flux.error(new IllegalArgumentException("messages is empty"));
        }
        try {
            validateRequired(provider, apiKey, baseUrl, model);
        } catch (IllegalArgumentException ex) {
            return Flux.error(ex);
        }
        if (Boolean.FALSE.equals(streamSupported)) {
            AiChatResponse resp = chat(request);
            return toOpenAiStream(resp.getContent());
        }

        switch (provider.toLowerCase(Locale.ROOT)) {
            case "anthropic":
                return streamAnthropic(baseUrl, apiKey, model, messages, request, aiApp, config);
            case "gemini":
                return streamGemini(baseUrl, apiKey, model, messages, request, aiApp, config);
            case "ollama":
                return streamOllama(baseUrl, model, messages, request, config);
            case "openai":
            case "deepseek":
            case "qwen":
            default:
                return streamOpenAiCompatible(baseUrl, apiKey, model, messages, request, config);
        }
    }

    private AiApp resolveApp(String appCode) {
        if (StringUtils.isBlank(appCode)) {
            return null;
        }
        return aiAppService.getOne(new LambdaQueryWrapper<AiApp>()
                .eq(AiApp::getCode, appCode)
                .eq(AiApp::getStatus, 1)
                .last("limit 1"));
    }

    private AiProviderConfig resolveProviderConfig(AiApp aiApp, String providerCode) {
        if (aiApp != null && aiApp.getProviderId() != null) {
            AiProviderConfig config = aiProviderConfigService.getById(aiApp.getProviderId());
            if (config != null && Objects.equals(config.getStatus(), 1)) {
                return config;
            }
        }
        if (StringUtils.isNotBlank(providerCode)) {
            AiProviderConfig config = aiProviderConfigService.getOne(new LambdaQueryWrapper<AiProviderConfig>()
                    .eq(AiProviderConfig::getCode, providerCode)
                    .eq(AiProviderConfig::getStatus, 1)
                    .last("limit 1"));
            if (config != null) {
                return config;
            }
        }
        throw new IllegalArgumentException("provider config not found");
    }

    private Map<String, Object> parseConfig(String configJson) {
        if (StringUtils.isBlank(configJson)) {
            return new HashMap<>();
        }
        Map<String, Object> map = JacksonUtils.toBean(configJson, new TypeReference<Map<String, Object>>() {});
        return map == null ? new HashMap<>() : map;
    }

    private String pickProvider(AiProviderConfig providerConfig, Map<String, Object> config) {
        String provider = pickString(config, "provider", providerConfig.getProvider());
        if (StringUtils.isBlank(provider)) {
            throw new IllegalArgumentException("provider is blank");
        }
        return provider;
    }

    private String pickApiKey(AiProviderConfig providerConfig, Map<String, Object> config) {
        String apiKey = pickString(config, "apiKey", providerConfig.getApiKeyRef());
        return apiKey == null ? "" : apiKey;
    }

    private String pickBaseUrl(AiProviderConfig providerConfig, Map<String, Object> config) {
        String baseUrl = pickString(config, "baseUrl", providerConfig.getBaseUrl());
        return baseUrl == null ? "" : baseUrl;
    }

    private String pickModel(AiChatRequest request, AiApp aiApp, AiProviderConfig providerConfig, Map<String, Object> config) {
        if (StringUtils.isNotBlank(request.getModel())) {
            return request.getModel();
        }
        String model = pickString(config, "model", providerConfig.getDefaultModel());
        return model == null ? "" : model;
    }

    private String normalizeModel(String provider, String model) {
        if (StringUtils.isNotBlank(model)) {
            return model;
        }
        if (provider == null) {
            return "";
        }
        switch (provider.toLowerCase(Locale.ROOT)) {
            case "deepseek":
                return "deepseek-chat";
            case "gemini":
                return "gemini-pro";
            default:
                return "";
        }
    }

    private void validateRequired(String provider, String apiKey, String baseUrl, String model) {
        if (StringUtils.isBlank(baseUrl)) {
            throw new IllegalArgumentException("baseUrl is blank");
        }
        if (StringUtils.isBlank(model)) {
            throw new IllegalArgumentException("model is blank");
        }
        if (provider == null) {
            return;
        }
        switch (provider.toLowerCase(Locale.ROOT)) {
            case "openai":
            case "deepseek":
            case "qwen":
            case "anthropic":
            case "gemini":
                if (StringUtils.isBlank(apiKey)) {
                    throw new IllegalArgumentException("apiKey is blank");
                }
                break;
            case "ollama":
            default:
                break;
        }
    }

    private List<AiChatRequest.Message> buildMessages(AiChatRequest request, AiApp aiApp) {
        List<AiChatRequest.Message> messages = new ArrayList<>();
        if (request.getMessages() != null) {
            messages.addAll(request.getMessages());
        }
        if (messages.isEmpty() && StringUtils.isNotBlank(request.getInput())) {
            AiChatRequest.Message msg = new AiChatRequest.Message();
            msg.setRole("user");
            msg.setContent(request.getInput());
            messages.add(msg);
        }
        if (aiApp != null && StringUtils.isNotBlank(aiApp.getSystemPrompt())) {
            boolean hasSystem = messages.stream().anyMatch(m -> "system".equalsIgnoreCase(m.getRole()));
            if (!hasSystem) {
                AiChatRequest.Message system = new AiChatRequest.Message();
                system.setRole("system");
                system.setContent(aiApp.getSystemPrompt());
                messages.add(0, system);
            }
        }
        return messages;
    }

    private AiChatResponse callOpenAiCompatible(String provider, String baseUrl, String apiKey, String model,
                                                List<AiChatRequest.Message> messages, AiChatRequest request,
                                                AiApp aiApp, Map<String, Object> config, Integer timeoutMs) {
        String path = pickString(config, "openaiPath", "/v1/chat/completions");
        String url = joinUrl(baseUrl, path);

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", toOpenAiMessages(messages));
        putIfNotNull(body, "temperature", pickDouble(config, "temperature", request.getTemperature()));
        putIfNotNull(body, "top_p", pickDouble(config, "topP", request.getTopP()));
        putIfNotNull(body, "max_tokens", pickInteger(config, "maxTokens", request.getMaxTokens()));
        putIfNotNull(body, "stream", request.getStream());

        Map<String, String> headers = new HashMap<>();
        if (StringUtils.isNotBlank(apiKey)) {
            headers.put("Authorization", "Bearer " + apiKey);
        }
        String raw = postJson(url, headers, body, timeoutMs);
        String content = extractOpenAiContent(raw);
        return buildResponse(provider, model, content, raw);
    }

    private AiChatResponse callAnthropic(String provider, String baseUrl, String apiKey, String model,
                                         List<AiChatRequest.Message> messages, AiChatRequest request,
                                         AiApp aiApp, Map<String, Object> config, Integer timeoutMs) {
        String path = pickString(config, "anthropicPath", "/v1/messages");
        String url = joinUrl(baseUrl, path);
        String version = pickString(config, "anthropicVersion", "2023-06-01");

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", toAnthropicMessages(messages));
        putIfNotNull(body, "max_tokens", pickInteger(config, "maxTokens", request.getMaxTokens(), 1024));
        putIfNotNull(body, "temperature", pickDouble(config, "temperature", request.getTemperature()));
        putIfNotNull(body, "top_p", pickDouble(config, "topP", request.getTopP()));
        String system = pickSystemPrompt(messages, aiApp);
        if (StringUtils.isNotBlank(system)) {
            body.put("system", system);
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("x-api-key", apiKey);
        headers.put("anthropic-version", version);
        String raw = postJson(url, headers, body, timeoutMs);
        String content = extractAnthropicContent(raw);
        return buildResponse(provider, model, content, raw);
    }

    private AiChatResponse callGemini(String provider, String baseUrl, String apiKey, String model,
                                      List<AiChatRequest.Message> messages, AiChatRequest request,
                                      AiApp aiApp, Map<String, Object> config, Integer timeoutMs) {
        String apiVersion = pickString(config, "geminiApiVersion", "v1beta");
        if (StringUtils.isBlank(model)) {
            model = "gemini-pro";
        }
        String path = "/" + apiVersion + "/models/" + model + ":generateContent";
        String url = joinUrl(baseUrl, path);
        if (StringUtils.isNotBlank(apiKey)) {
            url = url + "?key=" + apiKey;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("contents", toGeminiContents(messages));
        Map<String, Object> generationConfig = new HashMap<>();
        putIfNotNull(generationConfig, "temperature", pickDouble(config, "temperature", request.getTemperature()));
        putIfNotNull(generationConfig, "topP", pickDouble(config, "topP", request.getTopP()));
        putIfNotNull(generationConfig, "maxOutputTokens", pickInteger(config, "maxTokens", request.getMaxTokens()));
        if (!generationConfig.isEmpty()) {
            body.put("generationConfig", generationConfig);
        }
        String system = pickSystemPrompt(messages, aiApp);
        if (StringUtils.isNotBlank(system)) {
            Map<String, Object> systemInstruction = new HashMap<>();
            systemInstruction.put("parts", List.of(Map.of("text", system)));
            body.put("systemInstruction", systemInstruction);
        }

        String raw = postJson(url, Collections.emptyMap(), body, timeoutMs);
        String content = extractGeminiContent(raw);
        return buildResponse(provider, model, content, raw);
    }

    private AiChatResponse callOllama(String provider, String baseUrl, String model,
                                      List<AiChatRequest.Message> messages, AiChatRequest request,
                                      Map<String, Object> config, Integer timeoutMs) {
        String path = pickString(config, "ollamaPath", "/api/chat");
        String url = joinUrl(baseUrl, path);

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", toOpenAiMessages(messages));
        putIfNotNull(body, "stream", request.getStream() != null ? request.getStream() : Boolean.FALSE);

        String raw = postJson(url, Collections.emptyMap(), body, timeoutMs);
        String content = extractOllamaContent(raw);
        return buildResponse(provider, model, content, raw);
    }

    private Flux<String> streamOpenAiCompatible(String baseUrl, String apiKey, String model,
                                                List<AiChatRequest.Message> messages, AiChatRequest request,
                                                Map<String, Object> config) {
        String path = pickString(config, "openaiPath", "/chat/completions");
        String url = joinUrl(baseUrl, path);

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", toOpenAiMessages(messages));
        body.put("stream", true);
        putIfNotNull(body, "temperature", pickDouble(config, "temperature", request.getTemperature()));
        putIfNotNull(body, "top_p", pickDouble(config, "topP", request.getTopP()));
        putIfNotNull(body, "max_tokens", pickInteger(config, "maxTokens", request.getMaxTokens()));

        WebClient.RequestBodySpec req = buildWebClient()
                .post()
                .uri(url)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .contentType(MediaType.APPLICATION_JSON);

        if (StringUtils.isNotBlank(apiKey)) {
            req.header("Authorization", "Bearer " + apiKey);
        }

        return req.bodyValue(body)
                .retrieve()
                .bodyToFlux(DataBuffer.class)
                .map(this::bufferToString)
                .transform(this::sseToPayloadFlux);
    }

    private Flux<String> streamAnthropic(String baseUrl, String apiKey, String model,
                                         List<AiChatRequest.Message> messages, AiChatRequest request,
                                         AiApp aiApp, Map<String, Object> config) {
        String path = pickString(config, "anthropicPath", "/v1/messages");
        String url = joinUrl(baseUrl, path);
        String version = pickString(config, "anthropicVersion", "2023-06-01");

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", toAnthropicMessages(messages));
        body.put("stream", true);
        putIfNotNull(body, "max_tokens", pickInteger(config, "maxTokens", request.getMaxTokens(), 1024));
        putIfNotNull(body, "temperature", pickDouble(config, "temperature", request.getTemperature()));
        putIfNotNull(body, "top_p", pickDouble(config, "topP", request.getTopP()));
        String system = pickSystemPrompt(messages, aiApp);
        if (StringUtils.isNotBlank(system)) {
            body.put("system", system);
        }

        return buildWebClient()
                .post()
                .uri(url)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .contentType(MediaType.APPLICATION_JSON)
                .header("x-api-key", apiKey)
                .header("anthropic-version", version)
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(DataBuffer.class)
                .map(this::bufferToString)
                .transform(this::sseToPayloadFlux);
    }

    private Flux<String> streamGemini(String baseUrl, String apiKey, String model,
                                      List<AiChatRequest.Message> messages, AiChatRequest request,
                                      AiApp aiApp, Map<String, Object> config) {
        String apiVersion = pickString(config, "geminiApiVersion", "v1beta");
        if (StringUtils.isBlank(model)) {
            model = "gemini-pro";
        }
        String path = "/" + apiVersion + "/models/" + model + ":streamGenerateContent";
        String url = joinUrl(baseUrl, path);
        if (StringUtils.isNotBlank(apiKey)) {
            url = url + "?key=" + apiKey;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("contents", toGeminiContents(messages));
        Map<String, Object> generationConfig = new HashMap<>();
        putIfNotNull(generationConfig, "temperature", pickDouble(config, "temperature", request.getTemperature()));
        putIfNotNull(generationConfig, "topP", pickDouble(config, "topP", request.getTopP()));
        putIfNotNull(generationConfig, "maxOutputTokens", pickInteger(config, "maxTokens", request.getMaxTokens()));
        if (!generationConfig.isEmpty()) {
            body.put("generationConfig", generationConfig);
        }
        String system = pickSystemPrompt(messages, aiApp);
        if (StringUtils.isNotBlank(system)) {
            Map<String, Object> systemInstruction = new HashMap<>();
            systemInstruction.put("parts", List.of(Map.of("text", system)));
            body.put("systemInstruction", systemInstruction);
        }

        return buildWebClient()
                .post()
                .uri(url)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(DataBuffer.class)
                .map(this::bufferToString)
                .transform(this::sseToPayloadFlux);
    }

    private Flux<String> streamOllama(String baseUrl, String model,
                                      List<AiChatRequest.Message> messages, AiChatRequest request,
                                      Map<String, Object> config) {
        String path = pickString(config, "ollamaPath", "/api/chat");
        String url = joinUrl(baseUrl, path);

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", toOpenAiMessages(messages));
        body.put("stream", true);

        return buildWebClient()
                .post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(DataBuffer.class)
                .map(this::bufferToString)
                .transform(this::linesToFlux);
    }

    private String postJson(String url, Map<String, String> headers, Map<String, Object> body, Integer timeoutMs) {
        RestTemplate restTemplate = buildRestTemplate(timeoutMs);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        if (headers != null) {
            headers.forEach(httpHeaders::add);
        }
        HttpEntity<String> entity = new HttpEntity<>(JacksonUtils.toJson(body), httpHeaders);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        return response.getBody();
    }

    private RestTemplate buildRestTemplate(Integer timeoutMs) {
        int timeout = timeoutMs == null || timeoutMs <= 0 ? DEFAULT_TIMEOUT_MS : timeoutMs;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);
        return new RestTemplate(factory);
    }

    WebClient buildWebClient() {
        return WebClient.builder().build();
    }

    private List<Map<String, Object>> toOpenAiMessages(List<AiChatRequest.Message> messages) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (AiChatRequest.Message message : messages) {
            if (StringUtils.isBlank(message.getContent())) {
                continue;
            }
            Map<String, Object> item = new HashMap<>();
            item.put("role", message.getRole());
            item.put("content", message.getContent());
            list.add(item);
        }
        return list;
    }

    private List<Map<String, Object>> toAnthropicMessages(List<AiChatRequest.Message> messages) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (AiChatRequest.Message message : messages) {
            if ("system".equalsIgnoreCase(message.getRole())) {
                continue;
            }
            if (StringUtils.isBlank(message.getContent())) {
                continue;
            }
            Map<String, Object> item = new HashMap<>();
            item.put("role", message.getRole());
            item.put("content", message.getContent());
            list.add(item);
        }
        return list;
    }

    private List<Map<String, Object>> toGeminiContents(List<AiChatRequest.Message> messages) {
        List<Map<String, Object>> contents = new ArrayList<>();
        for (AiChatRequest.Message message : messages) {
            if ("system".equalsIgnoreCase(message.getRole())) {
                continue;
            }
            if (StringUtils.isBlank(message.getContent())) {
                continue;
            }
            Map<String, Object> content = new HashMap<>();
            String role = "assistant".equalsIgnoreCase(message.getRole()) ? "model" : "user";
            content.put("role", role);
            content.put("parts", List.of(Map.of("text", message.getContent())));
            contents.add(content);
        }
        return contents;
    }

    private String extractOpenAiContent(String raw) {
        try {
            JsonNode root = JacksonUtils.getObjectMapper().readTree(raw);
            JsonNode node = root.at("/choices/0/message/content");
            if (node.isMissingNode()) {
                node = root.at("/choices/0/text");
            }
            return node.isMissingNode() ? "" : node.asText();
        } catch (Exception e) {
            log.error("parse openai response failed", e);
            return "";
        }
    }

    private String extractAnthropicContent(String raw) {
        try {
            JsonNode root = JacksonUtils.getObjectMapper().readTree(raw);
            JsonNode node = root.at("/content/0/text");
            return node.isMissingNode() ? "" : node.asText();
        } catch (Exception e) {
            log.error("parse anthropic response failed", e);
            return "";
        }
    }

    private String extractGeminiContent(String raw) {
        try {
            JsonNode root = JacksonUtils.getObjectMapper().readTree(raw);
            JsonNode node = root.at("/candidates/0/content/parts/0/text");
            return node.isMissingNode() ? "" : node.asText();
        } catch (Exception e) {
            log.error("parse gemini response failed", e);
            return "";
        }
    }

    private String extractOllamaContent(String raw) {
        try {
            JsonNode root = JacksonUtils.getObjectMapper().readTree(raw);
            JsonNode node = root.at("/message/content");
            return node.isMissingNode() ? "" : node.asText();
        } catch (Exception e) {
            log.error("parse ollama response failed", e);
            return "";
        }
    }

    private AiChatResponse buildResponse(String provider, String model, String content, String raw) {
        AiChatResponse response = new AiChatResponse();
        response.setProvider(provider);
        response.setModel(model);
        response.setContent(content);
        response.setRaw(raw);
        return response;
    }

    private String joinUrl(String baseUrl, String path) {
        if (StringUtils.isBlank(baseUrl)) {
            return path;
        }
        if (baseUrl.endsWith("/") && path.startsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1) + path;
        }
        if (!baseUrl.endsWith("/") && !path.startsWith("/")) {
            return baseUrl + "/" + path;
        }
        return baseUrl + path;
    }

    private String pickSystemPrompt(List<AiChatRequest.Message> messages, AiApp aiApp) {
        for (AiChatRequest.Message message : messages) {
            if ("system".equalsIgnoreCase(message.getRole()) && StringUtils.isNotBlank(message.getContent())) {
                return message.getContent();
            }
        }
        return aiApp != null ? aiApp.getSystemPrompt() : null;
    }

    private String pickString(Map<String, Object> config, String key, String fallback) {
        if (config != null && config.containsKey(key)) {
            Object val = config.get(key);
            if (val != null) {
                return String.valueOf(val);
            }
        }
        return fallback;
    }

    private Integer pickInteger(Map<String, Object> config, String key, Integer fallback) {
        if (config != null && config.containsKey(key)) {
            Object val = config.get(key);
            if (val instanceof Number) {
                return ((Number) val).intValue();
            }
            if (val != null) {
                try {
                    return Integer.parseInt(val.toString());
                } catch (Exception ignore) {
                    return fallback;
                }
            }
        }
        return fallback;
    }

    private Integer pickInteger(Map<String, Object> config, String key, Integer fallback, Integer defaultValue) {
        Integer value = pickInteger(config, key, fallback);
        return value == null ? defaultValue : value;
    }

    private Double pickDouble(Map<String, Object> config, String key, Double fallback) {
        if (config != null && config.containsKey(key)) {
            Object val = config.get(key);
            if (val instanceof Number) {
                return ((Number) val).doubleValue();
            }
            if (val != null) {
                try {
                    return Double.parseDouble(val.toString());
                } catch (Exception ignore) {
                    return fallback;
                }
            }
        }
        return fallback;
    }

    private Boolean pickBoolean(Map<String, Object> config, String key, Boolean fallback) {
        if (config != null && config.containsKey(key)) {
            Object val = config.get(key);
            if (val instanceof Boolean) {
                return (Boolean) val;
            }
            if (val != null) {
                return Boolean.parseBoolean(val.toString());
            }
        }
        return fallback;
    }

    private Flux<String> toOpenAiStream(String content) {
        if (StringUtils.isBlank(content)) {
            return Flux.just("[DONE]");
        }
        Map<String, Object> delta = new HashMap<>();
        delta.put("content", content);

        Map<String, Object> choice = new HashMap<>();
        choice.put("delta", delta);
        choice.put("index", 0);

        Map<String, Object> payload = new HashMap<>();
        payload.put("choices", List.of(choice));

        return Flux.just(JacksonUtils.toJson(payload), "[DONE]");
    }

    private void putIfNotNull(Map<String, Object> map, String key, Object value) {
        if (!ObjectUtils.isEmpty(value)) {
            map.put(key, value);
        }
    }

    private String bufferToString(DataBuffer buffer) {
        byte[] bytes = new byte[buffer.readableByteCount()];
        buffer.read(bytes);
        DataBufferUtils.release(buffer);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private Flux<String> sseToPayloadFlux(Flux<String> rawTextFlux) {
        return Flux.create(sink -> {
            StringBuilder buffer = new StringBuilder();

            rawTextFlux.subscribe(
                    part -> {
                        if (part == null) return;
                        buffer.append(part);

                        while (true) {
                            DelimiterHit hit = findEventDelimiter(buffer);
                            if (hit.index < 0) break;
                            String event = buffer.substring(0, hit.index);
                            buffer.delete(0, hit.index + hit.length);
                            emitDataLines(event, sink);
                        }
                    },
                    sink::error,
                    () -> {
                        if (buffer.length() > 0) {
                            emitDataLines(buffer.toString(), sink);
                        }
                        sink.complete();
                    }
            );
        });
    }

    private Flux<String> linesToFlux(Flux<String> rawTextFlux) {
        return Flux.create(sink -> {
            StringBuilder buffer = new StringBuilder();

            rawTextFlux.subscribe(
                    part -> {
                        if (part == null) return;
                        buffer.append(part);
                        while (true) {
                            int idx = buffer.indexOf("\n");
                            if (idx < 0) break;
                            String line = buffer.substring(0, idx).trim();
                            buffer.delete(0, idx + 1);
                            if (!line.isEmpty()) {
                                sink.next(line);
                            }
                        }
                    },
                    sink::error,
                    () -> {
                        String line = buffer.toString().trim();
                        if (!line.isEmpty()) {
                            sink.next(line);
                        }
                        sink.complete();
                    }
            );
        });
    }

    private List<String> extractDataLines(String chunk) {
        if (!org.springframework.util.StringUtils.hasText(chunk)) return List.of();

        String[] lines = chunk.split("\\r?\\n");
        List<String> out = new ArrayList<>();

        for (String line : lines) {
            if (!org.springframework.util.StringUtils.hasText(line)) continue;
            line = line.trim();

            if (line.startsWith(":")) continue;

            if (line.startsWith("data:")) {
                String data = line.substring(5).trim();
                if (org.springframework.util.StringUtils.hasText(data)) {
                    out.add(data);
                }
            }
        }
        return out;
    }

    private void emitDataLines(String event, reactor.core.publisher.FluxSink<String> sink) {
        List<String> dataLines = extractDataLines(event);
        for (String dataLine : dataLines) {
            sink.next(dataLine);
        }
    }

    private DelimiterHit findEventDelimiter(StringBuilder buffer) {
        int lfIdx = buffer.indexOf("\n\n");
        int crlfIdx = buffer.indexOf("\r\n\r\n");

        if (lfIdx < 0 && crlfIdx < 0) return new DelimiterHit(-1, 0);
        if (lfIdx < 0) return new DelimiterHit(crlfIdx, 4);
        if (crlfIdx < 0) return new DelimiterHit(lfIdx, 2);
        return (crlfIdx < lfIdx) ? new DelimiterHit(crlfIdx, 4) : new DelimiterHit(lfIdx, 2);
    }

    private static final class DelimiterHit {
        final int index;
        final int length;

        DelimiterHit(int index, int length) {
            this.index = index;
            this.length = length;
        }
    }
}
