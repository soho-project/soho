package work.soho.ai.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.vo.OptionVo;
import work.soho.ai.biz.domain.AiProxyConfig;
import work.soho.ai.biz.request.AiProxyBatchStatusRequest;
import work.soho.ai.biz.request.AiProxyBatchTestRequest;
import work.soho.ai.biz.service.AiProxyConfigService;
import work.soho.ai.biz.service.AiProxyRelayService;
import work.soho.ai.biz.utils.AiProxyLayerUtils;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;

import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI代理配置 Controller。
 */
@Api(value = "AI代理配置", tags = "AI代理配置")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/admin/aiProxyConfig")
public class AiProxyConfigController {
    private static final String DEFAULT_TEST_URL = "https://chatgpt.com";
    private static final int DEFAULT_TEST_TIMEOUT_MS = 30000;
    private static final int TUNNEL_CHECK_TIMEOUT_MS = 5000;

    private final AiProxyConfigService aiProxyConfigService;
    private final AiProxyRelayService aiProxyRelayService;

    /**
     * 查询代理列表。
     *
     * @param aiProxyConfig 查询参数
     * @param betweenCreatedTimeRequest 创建时间范围
     * @return 代理分页列表
     */
    @GetMapping("/list")
    @Node(value = "aiProxyConfig::list", name = "获取 AI代理配置 列表")
    @ApiOperation(value = "获取 AI代理配置 列表", notes = "获取 AI代理配置 列表")
    public R<PageSerializable<AiProxyConfig>> list(AiProxyConfig aiProxyConfig,
                                                    BetweenCreatedTimeRequest betweenCreatedTimeRequest) {
        PageUtils.startPage();
        LambdaQueryWrapper<AiProxyConfig> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotBlank(aiProxyConfig.getName()), AiProxyConfig::getName, aiProxyConfig.getName());
        lqw.like(StringUtils.isNotBlank(aiProxyConfig.getProvider()), AiProxyConfig::getProvider, aiProxyConfig.getProvider());
        lqw.like(StringUtils.isNotBlank(aiProxyConfig.getProxyType()), AiProxyConfig::getProxyType, aiProxyConfig.getProxyType());
        lqw.like(StringUtils.isNotBlank(aiProxyConfig.getProxyHost()), AiProxyConfig::getProxyHost, aiProxyConfig.getProxyHost());
        lqw.eq(aiProxyConfig.getStatus() != null, AiProxyConfig::getStatus, aiProxyConfig.getStatus());
        lqw.eq(aiProxyConfig.getWeight() != null, AiProxyConfig::getWeight, aiProxyConfig.getWeight());
        lqw.ge(betweenCreatedTimeRequest != null && betweenCreatedTimeRequest.getStartTime() != null,
                AiProxyConfig::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest != null && betweenCreatedTimeRequest.getEndTime() != null,
                AiProxyConfig::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(AiProxyConfig::getWeight).orderByAsc(AiProxyConfig::getId);
        return R.success(new PageSerializable<>(aiProxyConfigService.list(lqw)));
    }

    /**
     * 查询代理详情。
     *
     * @param id 主键ID
     * @return 代理详情
     */
    @GetMapping("/{id}")
    @Node(value = "aiProxyConfig::getInfo", name = "获取 AI代理配置 详情")
    @ApiOperation(value = "获取 AI代理配置 详情", notes = "获取 AI代理配置 详情")
    public R<AiProxyConfig> getInfo(@PathVariable("id") Long id) {
        return R.success(aiProxyConfigService.getById(id));
    }

    /**
     * 新增代理配置。
     *
     * @param aiProxyConfig 代理配置
     * @return 是否成功
     */
    @PostMapping
    @Node(value = "aiProxyConfig::add", name = "新增 AI代理配置")
    @ApiOperation(value = "新增 AI代理配置", notes = "新增 AI代理配置")
    public R<Boolean> add(@RequestBody AiProxyConfig aiProxyConfig) {
        normalizeProvider(aiProxyConfig);
        return R.success(aiProxyConfigService.save(aiProxyConfig));
    }

    /**
     * 修改代理配置。
     *
     * @param aiProxyConfig 代理配置
     * @return 是否成功
     */
    @PutMapping
    @Node(value = "aiProxyConfig::edit", name = "修改 AI代理配置")
    @ApiOperation(value = "修改 AI代理配置", notes = "修改 AI代理配置")
    public R<Boolean> edit(@RequestBody AiProxyConfig aiProxyConfig) {
        normalizeProvider(aiProxyConfig);
        return R.success(aiProxyConfigService.updateById(aiProxyConfig));
    }

    /**
     * 删除代理配置。
     *
     * @param ids 主键ID数组
     * @return 是否成功
     */
    @DeleteMapping("/{ids}")
    @Node(value = "aiProxyConfig::remove", name = "删除 AI代理配置")
    @ApiOperation(value = "删除 AI代理配置", notes = "删除 AI代理配置")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(aiProxyConfigService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 批量修改代理状态。
     *
     * @param request 批量状态请求
     * @return 是否成功
     */
    @PutMapping("/batchStatus")
    @Node(value = "aiProxyConfig::batchStatus", name = "批量修改 AI代理配置 状态")
    @ApiOperation(value = "批量修改 AI代理配置 状态", notes = "按ID批量启用或禁用代理节点")
    public R<Boolean> batchStatus(@RequestBody AiProxyBatchStatusRequest request) {
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            return R.error("ids不能为空");
        }
        if (request.getStatus() == null || (request.getStatus() != 0 && request.getStatus() != 1)) {
            return R.error("status只能是0或1");
        }
        List<AiProxyConfig> list = aiProxyConfigService.listByIds(request.getIds());
        if (list.isEmpty()) {
            return R.error("未找到可更新的代理节点");
        }
        for (AiProxyConfig item : list) {
            item.setStatus(request.getStatus());
        }
        return R.success(aiProxyConfigService.updateBatchById(list));
    }

    /**
     * 批量测试代理连通性。
     *
     * @param request 批量测试请求
     * @return 测试结果
     */
    @PostMapping("/batchTest")
    @Node(value = "aiProxyConfig::batchTest", name = "批量测试 AI代理配置 连通性")
    @ApiOperation(value = "批量测试 AI代理配置 连通性", notes = "默认测试 https://chatgpt.com")
    public R<AiProxyBatchTestResponse> batchTest(@RequestBody AiProxyBatchTestRequest request) {
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            return R.error("ids不能为空");
        }
        String testUrl = StringUtils.isBlank(request.getTestUrl()) ? DEFAULT_TEST_URL : request.getTestUrl().trim();
        if (!isHttpUrl(testUrl)) {
            return R.error("testUrl必须是http或https地址");
        }
        int timeoutMs = request.getTimeoutMs() == null || request.getTimeoutMs() <= 0
                ? DEFAULT_TEST_TIMEOUT_MS : request.getTimeoutMs();

        List<AiProxyConfig> list = aiProxyConfigService.listByIds(request.getIds());
        if (list.isEmpty()) {
            return R.error("未找到可测试的代理节点");
        }

        AiProxyBatchTestResponse response = new AiProxyBatchTestResponse();
        response.setTestUrl(testUrl);
        response.setTimeoutMs(timeoutMs);
        List<AiProxyNodeTestResult> results = new ArrayList<>();
        int successCount = 0;

        for (AiProxyConfig item : list) {
            AiProxyNodeTestResult result = testSingleProxy(item, testUrl, timeoutMs);
            if (Boolean.TRUE.equals(result.getSuccess())) {
                successCount++;
            }
            results.add(result);
        }
        response.setResults(results);
        response.setTotal(results.size());
        response.setSuccessCount(successCount);
        response.setFailedCount(results.size() - successCount);
        return R.success(response);
    }

    /**
     * 获取下拉选项。
     *
     * @return 选项列表
     */
    @GetMapping("/options")
    @Node(value = "aiProxyConfig::options", name = "获取 AI代理配置 选项")
    @ApiOperation(value = "获取 AI代理配置 选项", notes = "获取 AI代理配置 选项")
    public R<List<OptionVo<Long, String>>> options() {
        List<AiProxyConfig> list = aiProxyConfigService.list(new LambdaQueryWrapper<AiProxyConfig>()
                .eq(AiProxyConfig::getStatus, 1)
                .orderByDesc(AiProxyConfig::getWeight)
                .orderByAsc(AiProxyConfig::getId));
        List<OptionVo<Long, String>> options = new ArrayList<>();
        for (AiProxyConfig item : list) {
            String label = StringUtils.isBlank(item.getName()) ? "代理#" + item.getId() : item.getName();
            if (StringUtils.isNotBlank(item.getProvider())) {
                label = label + " (" + item.getProvider() + ")";
            }
            OptionVo<Long, String> option = new OptionVo<>();
            option.setLabel(label);
            option.setValue(item.getId());
            options.add(option);
        }
        return R.success(options);
    }

    /**
     * 规范化供应商字段。
     *
     * @param aiProxyConfig 代理配置
     */
    private void normalizeProvider(AiProxyConfig aiProxyConfig) {
        if (aiProxyConfig == null) {
            return;
        }
        if (StringUtils.isNotBlank(aiProxyConfig.getProvider())) {
            aiProxyConfig.setProvider(aiProxyConfig.getProvider().trim().toLowerCase());
        }
    }

    /**
     * 测试单个代理节点连通性。
     *
     * @param item 代理节点
     * @param testUrl 测试地址
     * @param timeoutMs 超时时间
     * @return 测试结果
     */
    private AiProxyNodeTestResult testSingleProxy(AiProxyConfig item, String testUrl, int timeoutMs) {
        AiProxyNodeTestResult result = new AiProxyNodeTestResult();
        result.setId(item.getId());
        result.setName(item.getName());
        result.setProvider(item.getProvider());
        result.setProxyType(item.getProxyType());
        result.setProxyHost(item.getProxyHost());
        result.setProxyPort(item.getProxyPort());
        result.setSuccess(false);
        try {
            Map<String, Object> configMap = toProxyConfigMap(item);
            AiProxyLayerUtils.ProxySettings settings = AiProxyLayerUtils.resolve(configMap);
            if (settings == null) {
                result.setMessage("proxy config incomplete");
                return result;
            }
            AiProxyLayerUtils.ProxySettings resolvedSettings = aiProxyRelayService.ensureRelay(settings, item.getProvider());
            checkProxyTunnel(resolvedSettings, timeoutMs);
            Proxy proxy = AiProxyLayerUtils.buildJavaProxy(resolvedSettings);
            HttpURLConnection connection = (HttpURLConnection) new URL(testUrl).openConnection(proxy);
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(timeoutMs);
            connection.setReadTimeout(timeoutMs);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("User-Agent", "soho-ai-proxy-tester/1.0");
            connection.setRequestProperty("Accept", "*/*");
            if (resolvedSettings.isHttpProxy()
                    && StringUtils.isNotBlank(resolvedSettings.getUsername())
                    && StringUtils.isNotBlank(resolvedSettings.getPassword())) {
                String auth = resolvedSettings.getUsername() + ":" + resolvedSettings.getPassword();
                String basic = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
                connection.setRequestProperty("Proxy-Authorization", "Basic " + basic);
            }
            int statusCode = connection.getResponseCode();
            result.setStatusCode(statusCode);
            result.setSuccess(true);
            result.setMessage("ok");
            connection.disconnect();
            return result;
        } catch (SocketTimeoutException ex) {
            result.setSuccess(true);
            result.setMessage("proxy tunnel reachable, but target response timed out: " + ex.getMessage());
            return result;
        } catch (Exception ex) {
            result.setMessage(ex.getMessage());
            return result;
        }
    }

    /**
     * 检查代理隧道是否可连，避免目标站点慢响应导致误判。
     *
     * @param settings 代理设置
     * @param timeoutMs 超时时间
     */
    private void checkProxyTunnel(AiProxyLayerUtils.ProxySettings settings, int timeoutMs) {
        int tunnelTimeout = Math.min(Math.max(timeoutMs, 1000), TUNNEL_CHECK_TIMEOUT_MS);
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(settings.getHost(), settings.getPort()), tunnelTimeout);
        } catch (Exception ex) {
            throw new IllegalStateException("proxy tunnel unreachable: " + ex.getMessage(), ex);
        }
    }

    /**
     * 将代理实体映射为代理解析配置。
     *
     * @param item 代理实体
     * @return 配置Map
     */
    private Map<String, Object> toProxyConfigMap(AiProxyConfig item) {
        Map<String, Object> map = new HashMap<>();
        if (item == null) {
            return map;
        }
        putIfNotBlank(map, "proxyType", item.getProxyType());
        putIfNotBlank(map, "proxyHost", item.getProxyHost());
        if (item.getProxyPort() != null) {
            map.put("proxyPort", item.getProxyPort());
        }
        putIfNotBlank(map, "proxyUrl", item.getProxyUrl());
        putIfNotBlank(map, "proxyUsername", item.getProxyUsername());
        putIfNotBlank(map, "proxyPassword", item.getProxyPassword());
        return map;
    }

    /**
     * 非空字符串写入 Map。
     *
     * @param map map
     * @param key key
     * @param value value
     */
    private void putIfNotBlank(Map<String, Object> map, String key, String value) {
        if (StringUtils.isNotBlank(value)) {
            map.put(key, value.trim());
        }
    }

    /**
     * 校验是否为HTTP/HTTPS地址。
     *
     * @param testUrl 测试地址
     * @return 是否合法
     */
    private boolean isHttpUrl(String testUrl) {
        try {
            URI uri = URI.create(testUrl);
            String scheme = uri.getScheme();
            return "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 代理批量测试响应。
     */
    @lombok.Data
    public static class AiProxyBatchTestResponse {
        /**
         * 测试地址。
         */
        private String testUrl;

        /**
         * 超时时间。
         */
        private Integer timeoutMs;

        /**
         * 总数。
         */
        private Integer total;

        /**
         * 成功数。
         */
        private Integer successCount;

        /**
         * 失败数。
         */
        private Integer failedCount;

        /**
         * 明细结果。
         */
        private List<AiProxyNodeTestResult> results;
    }

    /**
     * 代理节点测试结果。
     */
    @lombok.Data
    public static class AiProxyNodeTestResult {
        /**
         * 节点ID。
         */
        private Long id;

        /**
         * 节点名称。
         */
        private String name;

        /**
         * 绑定供应商。
         */
        private String provider;

        /**
         * 协议类型。
         */
        private String proxyType;

        /**
         * 主机。
         */
        private String proxyHost;

        /**
         * 端口。
         */
        private Integer proxyPort;

        /**
         * 是否成功。
         */
        private Boolean success;

        /**
         * 状态码。
         */
        private Integer statusCode;

        /**
         * 消息。
         */
        private String message;
    }
}
