package work.soho.ai.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.vo.OptionVo;
import work.soho.ai.biz.domain.AiProxyConfig;
import work.soho.ai.biz.dto.AiProxyRuntimeStateSnapshot;
import work.soho.ai.biz.request.AiProxyBatchStatusRequest;
import work.soho.ai.biz.request.AiProxyBatchTestRequest;
import work.soho.ai.biz.service.AiProxyConfigService;
import work.soho.ai.biz.service.AiProxyRelayService;
import work.soho.ai.biz.service.AiProxyRuntimeStateService;
import work.soho.ai.biz.utils.AiProxyLayerUtils;
import work.soho.ai.biz.vo.AiProxyConfigMonitorVO;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Proxy;
import java.net.URI;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * AI代理配置 Controller。
 */
@Api(value = "AI代理配置", tags = "AI代理配置")
@RestController
@RequestMapping("/ai/admin/aiProxyConfig")
public class AiProxyConfigController {
    private static final String DEFAULT_TEST_URL = "https://chatgpt.com";
    private static final int DEFAULT_TEST_TIMEOUT_MS = 30000;
    private static final int TUNNEL_CHECK_TIMEOUT_MS = 5000;
    private static final int MAX_BATCH_TEST_CONCURRENCY = 5;
    private static final int BATCH_TEST_EXECUTOR_TERMINATION_SECONDS = 5;

    private final AiProxyConfigService aiProxyConfigService;
    private final AiProxyRelayService aiProxyRelayService;
    private final AiProxyRuntimeStateService aiProxyRuntimeStateService;

    /**
     * 创建代理配置控制器。
     *
     * @param aiProxyConfigService 代理配置服务
     * @param aiProxyRelayService 代理中继服务
     */
    public AiProxyConfigController(AiProxyConfigService aiProxyConfigService, AiProxyRelayService aiProxyRelayService) {
        this(aiProxyConfigService, aiProxyRelayService, null);
    }

    /**
     * 创建代理配置控制器。
     *
     * @param aiProxyConfigService 代理配置服务
     * @param aiProxyRelayService 代理中继服务
     * @param aiProxyRuntimeStateService 代理运行时状态服务
     */
    @Autowired
    public AiProxyConfigController(AiProxyConfigService aiProxyConfigService,
                                   AiProxyRelayService aiProxyRelayService,
                                   AiProxyRuntimeStateService aiProxyRuntimeStateService) {
        this.aiProxyConfigService = aiProxyConfigService;
        this.aiProxyRelayService = aiProxyRelayService;
        this.aiProxyRuntimeStateService = aiProxyRuntimeStateService;
    }

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
    public R<PageSerializable<AiProxyConfigMonitorVO>> list(AiProxyConfig aiProxyConfig,
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
        List<AiProxyConfig> list = aiProxyConfigService.list(lqw);
        return R.success(new PageSerializable<>(toMonitorViewList(list)));
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
    public R<AiProxyConfigMonitorVO> getInfo(@PathVariable("id") Long id) {
        AiProxyConfig config = aiProxyConfigService.getById(id);
        return R.success(toMonitorView(config, resolveStateSnapshot(config)));
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
     * 清理代理节点运行时状态。
     *
     * @param id 主键ID
     * @return 更新后的代理详情
     */
    @PostMapping("/{id}/clearRuntimeState")
    @Node(value = "aiProxyConfig::clearRuntimeState", name = "清理 AI代理配置 运行态")
    @ApiOperation(value = "清理 AI代理配置 运行态", notes = "清理单个代理节点的熔断、降权与统计运行态")
    public R<AiProxyConfigMonitorVO> clearRuntimeState(@PathVariable("id") Long id) {
        return R.success(resetRuntimeState(id));
    }

    /**
     * 手动解除代理节点熔断与降权。
     *
     * @param id 主键ID
     * @return 更新后的代理详情
     */
    @PostMapping("/{id}/resetCircuitBreaker")
    @Node(value = "aiProxyConfig::resetCircuitBreaker", name = "重置 AI代理配置 熔断")
    @ApiOperation(value = "重置 AI代理配置 熔断", notes = "手动解除单个代理节点的熔断与降权")
    public R<AiProxyConfigMonitorVO> resetCircuitBreaker(@PathVariable("id") Long id) {
        return R.success(resetRuntimeState(id));
    }

    /**
     * 转换代理监控视图列表。
     *
     * @param list 代理配置列表
     * @return 监控视图列表
     */
    private List<AiProxyConfigMonitorVO> toMonitorViewList(List<AiProxyConfig> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, AiProxyRuntimeStateSnapshot> stateMap = aiProxyRuntimeStateService == null
                ? new HashMap<>()
                : aiProxyRuntimeStateService.getStateSnapshotMap(list);
        List<AiProxyConfigMonitorVO> result = new ArrayList<>(list.size());
        for (AiProxyConfig item : list) {
            result.add(toMonitorView(item, stateMap.get(item.getId())));
        }
        return result;
    }

    /**
     * 转换单个代理监控视图。
     *
     * @param config 代理配置
     * @param snapshot 运行时快照
     * @return 监控视图
     */
    private AiProxyConfigMonitorVO toMonitorView(AiProxyConfig config, AiProxyRuntimeStateSnapshot snapshot) {
        if (config == null) {
            return null;
        }
        AiProxyConfigMonitorVO view = new AiProxyConfigMonitorVO();
        BeanUtils.copyProperties(config, view);
        if (snapshot == null) {
            return view;
        }
        view.setEffectiveWeight(snapshot.getEffectiveWeight());
        view.setRequestAllowed(snapshot.getRequestAllowed());
        view.setCircuitOpen(snapshot.getCircuitOpen());
        view.setCircuitOpenUntilMs(snapshot.getCircuitOpenUntilMs());
        view.setLastSuccessAtMs(snapshot.getLastSuccessAtMs());
        view.setLastFailureAtMs(snapshot.getLastFailureAtMs());
        view.setEwmaTotalMs(snapshot.getEwmaTotalMs());
        view.setConsecutiveFailures(snapshot.getConsecutiveFailures());
        view.setConsecutiveSlowRequests(snapshot.getConsecutiveSlowRequests());
        view.setLastErrorMessage(snapshot.getLastErrorMessage());
        view.setTotalSuccessCount(snapshot.getTotalSuccessCount());
        view.setTotalFailureCount(snapshot.getTotalFailureCount());
        return view;
    }

    /**
     * 查询单个代理运行时快照。
     *
     * @param config 代理配置
     * @return 运行时快照
     */
    private AiProxyRuntimeStateSnapshot resolveStateSnapshot(AiProxyConfig config) {
        if (aiProxyRuntimeStateService == null || config == null) {
            return null;
        }
        return aiProxyRuntimeStateService.getStateSnapshot(config);
    }

    /**
     * 重置代理运行时状态并返回最新监控视图。
     *
     * @param id 主键ID
     * @return 监控视图
     */
    private AiProxyConfigMonitorVO resetRuntimeState(Long id) {
        AiProxyConfig config = aiProxyConfigService.getById(id);
        if (config == null) {
            throw new IllegalArgumentException("代理节点不存在");
        }
        if (aiProxyRuntimeStateService != null) {
            aiProxyRuntimeStateService.clearState(id);
        }
        return toMonitorView(config, resolveStateSnapshot(config));
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

        ExecutorService executor = createBatchTestExecutor(list.size());
        try {
            List<CompletableFuture<AiProxyNodeTestResult>> futures = list.stream()
                    .map(item -> CompletableFuture
                            .supplyAsync(() -> testSingleProxy(item, testUrl, timeoutMs), executor)
                            .exceptionally(ex -> buildUnexpectedFailureResult(item, ex)))
                    .collect(Collectors.toList());

            List<AiProxyNodeTestResult> results = futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
            int successCount = (int) results.stream()
                    .filter(item -> Boolean.TRUE.equals(item.getSuccess()))
                    .count();

            response.setResults(results);
            response.setTotal(results.size());
            response.setSuccessCount(successCount);
            response.setFailedCount(results.size() - successCount);
            return R.success(response);
        } finally {
            shutdownBatchTestExecutor(executor);
        }
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
    AiProxyNodeTestResult testSingleProxy(AiProxyConfig item, String testUrl, int timeoutMs) {
        AiProxyNodeTestResult result = new AiProxyNodeTestResult();
        result.setId(item.getId());
        result.setName(item.getName());
        result.setProvider(item.getProvider());
        result.setProxyType(item.getProxyType());
        result.setProxyHost(item.getProxyHost());
        result.setProxyPort(item.getProxyPort());
        result.setSuccess(false);
        long targetStartNanos = -1L;
        try {
            Map<String, Object> configMap = toProxyConfigMap(item);
            AiProxyLayerUtils.ProxySettings settings = AiProxyLayerUtils.resolve(configMap);
            if (settings == null) {
                result.setMessage("proxy config incomplete");
                return result;
            }
            AiProxyLayerUtils.ProxySettings resolvedSettings = aiProxyRelayService.ensureRelay(settings, item.getProvider());
            fillResolvedProxyInfo(result, resolvedSettings);
            result.setTunnelLatencyMs(checkProxyTunnel(resolvedSettings, timeoutMs));
            URI targetUri = URI.create(testUrl);
            targetStartNanos = System.nanoTime();
            TargetProbeResponse probeResponse = executeTargetProbe(resolvedSettings, targetUri, timeoutMs);
            result.setTargetLatencyMs(elapsedMillis(targetStartNanos));
            result.setStatusCode(probeResponse.getStatusCode());
            result.setSuccess(isHealthyStatusCode(probeResponse.getStatusCode()));
            result.setMessage(Boolean.TRUE.equals(result.getSuccess()) ? "ok" : "target responded with status " + probeResponse.getStatusCode());
            return result;
        } catch (SocketTimeoutException ex) {
            if (targetStartNanos > 0) {
                result.setTargetLatencyMs(elapsedMillis(targetStartNanos));
            }
            result.setSuccess(false);
            result.setMessage("target response timed out: " + ex.getMessage());
            return result;
        } catch (SSLHandshakeException ex) {
            if (targetStartNanos > 0 && result.getTargetLatencyMs() == null) {
                result.setTargetLatencyMs(elapsedMillis(targetStartNanos));
            }
            result.setMessage(buildTlsFailureMessage(testUrl, ex));
            return result;
        } catch (Exception ex) {
            if (targetStartNanos > 0 && result.getTargetLatencyMs() == null) {
                result.setTargetLatencyMs(elapsedMillis(targetStartNanos));
            }
            result.setMessage(ex.getMessage());
            return result;
        }
    }

    /**
     * 执行目标站点探测，请求链路中的域名解析交给代理侧处理。
     *
     * @param settings 代理设置
     * @param targetUri 目标地址
     * @param timeoutMs 超时时间
     * @return 探测响应
     * @throws IOException IO异常
     */
    TargetProbeResponse executeTargetProbe(AiProxyLayerUtils.ProxySettings settings, URI targetUri, int timeoutMs) throws IOException {
        if (settings.isHttpProxy()) {
            return executeHttpProxyProbe(settings, targetUri, timeoutMs);
        }
        return executeSocks5Probe(settings, targetUri, timeoutMs);
    }

    /**
     * 通过 HTTP 代理执行目标探测。
     *
     * @param settings 代理设置
     * @param targetUri 目标地址
     * @param timeoutMs 超时时间
     * @return 探测响应
     * @throws IOException IO异常
     */
    private TargetProbeResponse executeHttpProxyProbe(AiProxyLayerUtils.ProxySettings settings, URI targetUri, int timeoutMs) throws IOException {
        int port = resolveTargetPort(targetUri);
        long startNanos = System.nanoTime();
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(settings.getHost(), settings.getPort()), Math.min(Math.max(timeoutMs, 1000), TUNNEL_CHECK_TIMEOUT_MS));
            long tunnelLatencyMs = elapsedMillis(startNanos);
            socket.setSoTimeout(timeoutMs);
            if (StringUtils.isNotBlank(settings.getUsername()) && StringUtils.isNotBlank(settings.getPassword())) {
                // CONNECT 与普通请求都由代理认证，避免前置 407 影响探测结论。
            }
            if (isHttpsTarget(targetUri)) {
                establishHttpConnectTunnel(socket, settings, targetUri.getHost(), port);
                return executeHttpsRequest(socket, targetUri, timeoutMs);
            }
            return executePlainHttpRequest(socket, true, targetUri);
        }
    }

    /**
     * 通过 SOCKS5 代理执行目标探测，域名以 ATYP=DOMAIN 形式交给代理解析。
     *
     * @param settings 代理设置
     * @param targetUri 目标地址
     * @param timeoutMs 超时时间
     * @return 探测响应
     * @throws IOException IO异常
     */
    private TargetProbeResponse executeSocks5Probe(AiProxyLayerUtils.ProxySettings settings, URI targetUri, int timeoutMs) throws IOException {
        int port = resolveTargetPort(targetUri);
        long startNanos = System.nanoTime();
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(settings.getHost(), settings.getPort()), Math.min(Math.max(timeoutMs, 1000), TUNNEL_CHECK_TIMEOUT_MS));
            socket.setSoTimeout(timeoutMs);
            performSocks5Handshake(socket, targetUri.getHost(), port);
            long tunnelLatencyMs = elapsedMillis(startNanos);
            return isHttpsTarget(targetUri)
                    ? executeHttpsRequest(socket, targetUri, timeoutMs)
                    : executePlainHttpRequest(socket, false, targetUri);
        }
    }

    /**
     * 检查代理隧道是否可连，并返回隧道耗时。
     *
     * @param settings 代理设置
     * @param timeoutMs 超时时间
     * @return 隧道耗时毫秒
     */
    Long checkProxyTunnel(AiProxyLayerUtils.ProxySettings settings, int timeoutMs) {
        int tunnelTimeout = Math.min(Math.max(timeoutMs, 1000), TUNNEL_CHECK_TIMEOUT_MS);
        long startNanos = System.nanoTime();
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(settings.getHost(), settings.getPort()), tunnelTimeout);
            return elapsedMillis(startNanos);
        } catch (Exception ex) {
            throw new IllegalStateException("proxy tunnel unreachable: " + ex.getMessage(), ex);
        }
    }

    /**
     * 建立 HTTP CONNECT 隧道。
     *
     * @param socket 代理socket
     * @param settings 代理设置
     * @param host 目标主机
     * @param port 目标端口
     * @throws IOException IO异常
     */
    private void establishHttpConnectTunnel(Socket socket, AiProxyLayerUtils.ProxySettings settings, String host, int port) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.ISO_8859_1));
        writer.write("CONNECT " + host + ":" + port + " HTTP/1.1\r\n");
        writer.write("Host: " + host + ":" + port + "\r\n");
        writer.write("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36\r\n");
        if (StringUtils.isNotBlank(settings.getUsername()) && StringUtils.isNotBlank(settings.getPassword())) {
            String auth = settings.getUsername() + ":" + settings.getPassword();
            String basic = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            writer.write("Proxy-Authorization: Basic " + basic + "\r\n");
        }
        writer.write("Proxy-Connection: Keep-Alive\r\n\r\n");
        writer.flush();
        String statusLine = readStatusLine(socket.getInputStream());
        if (StringUtils.isBlank(statusLine) || !statusLine.contains(" 200 ")) {
            throw new IOException("proxy connect failed: " + statusLine);
        }
        consumeHttpHeaders(socket.getInputStream());
    }

    /**
     * 执行 HTTPS 目标请求。
     *
     * @param socket 已建立隧道的socket
     * @param targetUri 目标地址
     * @param timeoutMs 超时时间
     * @return 探测响应
     * @throws IOException IO异常
     */
    private TargetProbeResponse executeHttpsRequest(Socket socket, URI targetUri, int timeoutMs) throws IOException {
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try (SSLSocket sslSocket = (SSLSocket) factory.createSocket(socket, targetUri.getHost(), resolveTargetPort(targetUri), true)) {
            sslSocket.setUseClientMode(true);
            sslSocket.setSoTimeout(timeoutMs);
            sslSocket.startHandshake();
            return writeAndReadHttpRequest(sslSocket.getOutputStream(), sslSocket.getInputStream(), targetUri, false);
        }
    }

    /**
     * 执行明文 HTTP 目标请求。
     *
     * @param socket 已连接socket
     * @param settings HTTP代理设置，直连隧道场景可为空
     * @param targetUri 目标地址
     * @return 探测响应
     * @throws IOException IO异常
     */
    private TargetProbeResponse executePlainHttpRequest(Socket socket, boolean useAbsoluteForm, URI targetUri) throws IOException {
        return writeAndReadHttpRequest(socket.getOutputStream(), socket.getInputStream(), targetUri, useAbsoluteForm);
    }

    /**
     * 写入浏览器风格 GET 请求并解析响应状态。
     *
     * @param outputStream 输出流
     * @param inputStream 输入流
     * @param targetUri 目标地址
     * @param useAbsoluteForm 是否使用 absolute-form 请求行
     * @return 探测响应
     * @throws IOException IO异常
     */
    private TargetProbeResponse writeAndReadHttpRequest(OutputStream outputStream, InputStream inputStream,
                                                        URI targetUri, boolean useAbsoluteForm) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.ISO_8859_1));
        writer.write("GET " + buildRequestPath(targetUri, useAbsoluteForm) + " HTTP/1.1\r\n");
        writer.write("Host: " + targetUri.getHost() + "\r\n");
        writer.write("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36\r\n");
        writer.write("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n");
        writer.write("Accept-Language: zh-CN,zh;q=0.9,en;q=0.8\r\n");
        writer.write("Cache-Control: no-cache\r\n");
        writer.write("Pragma: no-cache\r\n");
        writer.write("Connection: close\r\n\r\n");
        writer.flush();
        String statusLine = readStatusLine(inputStream);
        TargetProbeResponse response = new TargetProbeResponse();
        response.setStatusCode(parseStatusCode(statusLine));
        consumeHttpHeaders(inputStream);
        return response;
    }

    /**
     * 执行 SOCKS5 CONNECT，域名直接交给代理解析。
     *
     * @param socket 代理socket
     * @param host 目标主机
     * @param port 目标端口
     * @throws IOException IO异常
     */
    private void performSocks5Handshake(Socket socket, String host, int port) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();
        outputStream.write(new byte[]{0x05, 0x01, 0x00});
        outputStream.flush();
        byte[] greeting = readFully(inputStream, 2);
        if (greeting[0] != 0x05 || greeting[1] != 0x00) {
            throw new IOException("socks5 greeting rejected");
        }
        byte[] hostBytes = host.getBytes(StandardCharsets.UTF_8);
        byte[] request = new byte[7 + hostBytes.length];
        request[0] = 0x05;
        request[1] = 0x01;
        request[2] = 0x00;
        request[3] = 0x03;
        request[4] = (byte) hostBytes.length;
        System.arraycopy(hostBytes, 0, request, 5, hostBytes.length);
        request[5 + hostBytes.length] = (byte) ((port >> 8) & 0xff);
        request[6 + hostBytes.length] = (byte) (port & 0xff);
        outputStream.write(request);
        outputStream.flush();
        byte[] header = readFully(inputStream, 4);
        if (header[1] != 0x00) {
            throw new IOException("socks5 connect failed, code=" + (header[1] & 0xff));
        }
        int atyp = header[3] & 0xff;
        if (atyp == 0x01) {
            readFully(inputStream, 6);
        } else if (atyp == 0x03) {
            int length = inputStream.read();
            if (length < 0) {
                throw new IOException("socks5 reply truncated");
            }
            readFully(inputStream, length + 2);
        } else if (atyp == 0x04) {
            readFully(inputStream, 18);
        } else {
            throw new IOException("unsupported socks5 atyp=" + atyp);
        }
    }

    /**
     * 读取 HTTP 状态行。
     *
     * @param inputStream 输入流
     * @return 状态行
     * @throws IOException IO异常
     */
    private String readStatusLine(InputStream inputStream) throws IOException {
        return readAsciiLine(inputStream);
    }

    /**
     * 消费剩余响应头。
     *
     * @param inputStream 输入流
     * @throws IOException IO异常
     */
    private void consumeHttpHeaders(InputStream inputStream) throws IOException {
        String line;
        while ((line = readAsciiLine(inputStream)) != null) {
            if (line.isEmpty()) {
                break;
            }
        }
    }

    /**
     * 以 ASCII 方式逐字节读取一行，避免缓冲越界吞掉后续 TLS/响应字节。
     *
     * @param inputStream 输入流
     * @return 行内容
     * @throws IOException IO异常
     */
    private String readAsciiLine(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        int current;
        boolean hasData = false;
        while ((current = inputStream.read()) >= 0) {
            hasData = true;
            if (current == '\n') {
                break;
            }
            if (current != '\r') {
                builder.append((char) current);
            }
        }
        if (!hasData && builder.length() == 0) {
            return null;
        }
        return builder.toString();
    }

    /**
     * 读取固定长度字节。
     *
     * @param inputStream 输入流
     * @param length 长度
     * @return 字节数组
     * @throws IOException IO异常
     */
    private byte[] readFully(InputStream inputStream, int length) throws IOException {
        byte[] buffer = new byte[length];
        int offset = 0;
        while (offset < length) {
            int read = inputStream.read(buffer, offset, length - offset);
            if (read < 0) {
                throw new IOException("unexpected end of stream");
            }
            offset += read;
        }
        return buffer;
    }

    /**
     * 构造请求路径。
     *
     * @param targetUri 目标地址
     * @param useAbsoluteForm 是否使用 absolute-form
     * @return 请求路径
     */
    private String buildRequestPath(URI targetUri, boolean useAbsoluteForm) {
        if (useAbsoluteForm) {
            return targetUri.toString();
        }
        String path = StringUtils.isBlank(targetUri.getRawPath()) ? "/" : targetUri.getRawPath();
        if (StringUtils.isNotBlank(targetUri.getRawQuery())) {
            path = path + "?" + targetUri.getRawQuery();
        }
        return path;
    }

    /**
     * 解析目标端口。
     *
     * @param targetUri 目标地址
     * @return 端口
     */
    private int resolveTargetPort(URI targetUri) {
        if (targetUri.getPort() > 0) {
            return targetUri.getPort();
        }
        return isHttpsTarget(targetUri) ? 443 : 80;
    }

    /**
     * 是否为 HTTPS 目标。
     *
     * @param targetUri 目标地址
     * @return 是否 HTTPS
     */
    private boolean isHttpsTarget(URI targetUri) {
        return "https".equalsIgnoreCase(targetUri.getScheme());
    }

    /**
     * 解析 HTTP 状态码。
     *
     * @param statusLine 状态行
     * @return 状态码
     */
    private Integer parseStatusCode(String statusLine) {
        if (StringUtils.isBlank(statusLine)) {
            return null;
        }
        String[] parts = statusLine.split(" ");
        if (parts.length < 2) {
            return null;
        }
        try {
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * 构造 TLS 失败诊断信息。
     *
     * @param testUrl 测试地址
     * @param ex TLS异常
     * @return 诊断消息
     */
    private String buildTlsFailureMessage(String testUrl, SSLHandshakeException ex) {
        String message = ex.getMessage();
        if (StringUtils.isNotBlank(message) && message.contains("No subject alternative DNS name matching")) {
            return "tls hostname verification failed via proxy for " + testUrl + ": " + message;
        }
        return StringUtils.isBlank(message) ? "tls handshake failed" : "tls handshake failed: " + message;
    }

    /**
     * 目标探测响应。
     */
    @lombok.Data
    private static class TargetProbeResponse {
        private Integer statusCode;
    }

    /**
     * 回填最终生效的代理出口信息。
     *
     * @param result 测试结果
     * @param settings 解析后的代理设置
     */
    private void fillResolvedProxyInfo(AiProxyNodeTestResult result, AiProxyLayerUtils.ProxySettings settings) {
        if (result == null || settings == null) {
            return;
        }
        result.setResolvedProxyType(settings.isHttpProxy() ? "http" : "socks5");
        result.setResolvedHost(settings.getHost());
        result.setResolvedPort(settings.getPort());
        result.setRelayMode(settings.isLocalRelayRequired() ? "localRelay" : "directProxy");
    }

    /**
     * 构造批量测试中线程级异常的失败结果。
     *
     * @param item 代理节点
     * @param ex 异常
     * @return 失败结果
     */
    private AiProxyNodeTestResult buildUnexpectedFailureResult(AiProxyConfig item, Throwable ex) {
        AiProxyNodeTestResult result = new AiProxyNodeTestResult();
        if (item != null) {
            result.setId(item.getId());
            result.setName(item.getName());
            result.setProvider(item.getProvider());
            result.setProxyType(item.getProxyType());
            result.setProxyHost(item.getProxyHost());
            result.setProxyPort(item.getProxyPort());
        }
        result.setSuccess(false);
        Throwable cause = ex;
        while (cause != null && cause.getCause() != null) {
            cause = cause.getCause();
        }
        result.setMessage(cause == null ? "batch test failed" : cause.getMessage());
        return result;
    }

    /**
     * 关闭批量测试线程池。
     *
     * @param executor 线程池
     */
    private void shutdownBatchTestExecutor(ExecutorService executor) {
        if (executor == null) {
            return;
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(BATCH_TEST_EXECUTOR_TERMINATION_SECONDS, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 仅供测试验证使用，返回批量测试最大并发上限。
     *
     * @return 最大并发上限
     */
    static int getMaxBatchTestConcurrency() {
        return MAX_BATCH_TEST_CONCURRENCY;
    }

    /**
     * 仅供测试验证使用，返回批量测试线程池大小。
     *
     * @param candidateCount 候选节点数
     * @return 实际线程池大小
     */
    static int resolveBatchTestPoolSize(int candidateCount) {
        return Math.min(MAX_BATCH_TEST_CONCURRENCY, Math.max(candidateCount, 0));
    }

    /**
     * 仅供测试验证使用，按固定上限构造线程池。
     *
     * @param candidateCount 候选节点数
     * @return 线程池
     */
    static ExecutorService createBatchTestExecutor(int candidateCount) {
        return Executors.newFixedThreadPool(resolveBatchTestPoolSize(candidateCount));
    }

    /**
     * 判断目标站点响应状态是否健康。
     *
     * @param statusCode 状态码
     * @return 是否健康
     */
    private boolean isHealthyStatusCode(Integer statusCode) {
        return statusCode != null && statusCode >= 200 && statusCode < 400;
    }

    /**
     * 计算从起始时间到当前的耗时毫秒。
     *
     * @param startNanos 起始纳秒
     * @return 耗时毫秒
     */
    private long elapsedMillis(long startNanos) {
        return Math.max(0L, (System.nanoTime() - startNanos) / 1_000_000L);
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
         * 隧道探测耗时毫秒。
         */
        private Long tunnelLatencyMs;

        /**
         * 目标站点响应耗时毫秒。
         */
        private Long targetLatencyMs;

        /**
         * 最终生效的代理类型。
         */
        private String resolvedProxyType;

        /**
         * 最终生效的代理主机。
         */
        private String resolvedHost;

        /**
         * 最终生效的代理端口。
         */
        private Integer resolvedPort;

        /**
         * relay 模式。
         */
        private String relayMode;

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
