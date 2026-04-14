package work.soho.ai.biz.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import work.soho.ai.biz.service.AiProxyRelayService;
import work.soho.ai.biz.utils.AiProxyLayerUtils;
import work.soho.common.core.util.JacksonUtils;
import work.soho.common.core.util.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * AI 代理中继服务实现。
 *
 * 说明：
 * 1. 对 ss/vmess/vless/trojan/hysteria2 节点，自动拉起内置 xray 进程；
 * 2. xray 对外暴露本地 socks5 出口，业务层仅连接本地出口；
 * 3. 进程按节点复用，避免重复拉起。
 */
@Log4j2
@Service
public class AiProxyRelayServiceImpl implements AiProxyRelayService {
    private static final int RELAY_STARTUP_TIMEOUT_MS = 5000;
    private static final int RELAY_PORT_PROBE_TIMEOUT_MS = 200;
    private static final String LOCAL_RELAY_HOST = "127.0.0.1";
    private static final String DEFAULT_XRAY_BIN = "xray";
    private static final String DEFAULT_WORK_DIR = "/tmp/soho-ai-relay";
    private static final String KEY_XRAY_BIN_SYS = "ai.relay.xray.bin";
    private static final String KEY_XRAY_BIN_ENV = "AI_RELAY_XRAY_BIN";
    private static final String KEY_WORK_DIR_SYS = "ai.relay.workdir";
    private static final String KEY_WORK_DIR_ENV = "AI_RELAY_WORKDIR";

    private final ConcurrentMap<String, RelayProcessHolder> relayProcesses = new ConcurrentHashMap<>();

    /**
     * 确保中继可用，并返回标准 socks5 代理设置。
     *
     * @param settings 原始代理设置
     * @param provider 供应商编码
     * @return 可直连的代理设置
     */
    @Override
    public synchronized AiProxyLayerUtils.ProxySettings ensureRelay(AiProxyLayerUtils.ProxySettings settings, String provider) {
        if (settings == null || !settings.isLocalRelayRequired()) {
            return settings;
        }
        String protocol = normalizeProtocol(settings.getProtocol());
        String proxyUrl = settings.getProxyUrl();
        if (StringUtils.isBlank(proxyUrl)) {
            throw new IllegalArgumentException("proxyUrl required for protocol: " + protocol);
        }
        String relayKey = buildRelayKey(protocol, proxyUrl);
        String nodeSummary = summarizeRelayNode(protocol, proxyUrl);
        RelayProcessHolder holder = relayProcesses.get(relayKey);
        if (holder == null || !holder.isAlive()) {
            log.info("relay node preparing, provider={}, protocol={}, relayKey={}, node={}",
                    provider, protocol, relayKey, nodeSummary);
            holder = startRelayProcess(relayKey, protocol, proxyUrl, provider, nodeSummary);
            relayProcesses.put(relayKey, holder);
        }
        return buildSocks5RelaySettings(holder.getPort());
    }

    /**
     * 启动中继进程。
     *
     * @param relayKey 中继键
     * @param protocol 协议
     * @param proxyUrl 节点URL
     * @param provider 供应商
     * @return 中继进程句柄
     */
    private RelayProcessHolder startRelayProcess(String relayKey, String protocol, String proxyUrl,
                                                 String provider, String nodeSummary) {
        int localPort = allocatePort();
        Path workDir = ensureWorkDir();
        Path configPath = workDir.resolve("relay-" + relayKey + ".json");
        Path logPath = workDir.resolve("relay-" + relayKey + ".log");
        Map<String, Object> xrayConfig = buildXrayConfig(protocol, proxyUrl, localPort);
        writeConfig(configPath, xrayConfig);
        Process process = startProcess(configPath, logPath);
        waitRelayStarted(localPort, process, relayKey, logPath, protocol, nodeSummary);
        log.info("relay started, provider={}, protocol={}, relayKey={}, node={}, local={}:{}",
                provider, protocol, relayKey, nodeSummary, LOCAL_RELAY_HOST, localPort);
        return new RelayProcessHolder(process, localPort);
    }

    /**
     * 生成中继键。
     *
     * @param protocol 协议
     * @param proxyUrl 节点URL
     * @return 中继键
     */
    private String buildRelayKey(String protocol, String proxyUrl) {
        return sha256(protocol + "::" + proxyUrl.trim());
    }

    /**
     * 创建 socks5 代理设置。
     *
     * @param port 本地端口
     * @return 代理设置
     */
    private AiProxyLayerUtils.ProxySettings buildSocks5RelaySettings(int port) {
        Map<String, Object> relayConfig = new HashMap<>();
        relayConfig.put("proxyType", "socks5");
        relayConfig.put("proxyHost", LOCAL_RELAY_HOST);
        relayConfig.put("proxyPort", port);
        return AiProxyLayerUtils.resolve(relayConfig);
    }

    /**
     * 生成 xray 配置。
     *
     * @param protocol 协议
     * @param proxyUrl 节点URL
     * @param localPort 本地端口
     * @return xray 配置
     */
    private Map<String, Object> buildXrayConfig(String protocol, String proxyUrl, int localPort) {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("log", Collections.singletonMap("loglevel", "warning"));
        root.put("inbounds", Collections.singletonList(buildInbound(localPort)));
        root.put("outbounds", buildOutbounds(protocol, proxyUrl));
        root.put("routing", buildRouting());
        return root;
    }

    /**
     * 构建 inbound。
     *
     * @param localPort 本地端口
     * @return inbound 配置
     */
    private Map<String, Object> buildInbound(int localPort) {
        Map<String, Object> inbound = new LinkedHashMap<>();
        inbound.put("tag", "in-socks");
        inbound.put("listen", LOCAL_RELAY_HOST);
        inbound.put("port", localPort);
        inbound.put("protocol", "socks");
        Map<String, Object> settings = new LinkedHashMap<>();
        settings.put("auth", "noauth");
        settings.put("udp", true);
        inbound.put("settings", settings);
        return inbound;
    }

    /**
     * 构建 outbounds。
     *
     * @param protocol 协议
     * @param proxyUrl 节点URL
     * @return outbounds
     */
    private List<Map<String, Object>> buildOutbounds(String protocol, String proxyUrl) {
        List<Map<String, Object>> outbounds = new ArrayList<>();
        outbounds.add(buildProxyOutbound(protocol, proxyUrl));
        Map<String, Object> direct = new LinkedHashMap<>();
        direct.put("tag", "direct");
        direct.put("protocol", "freedom");
        outbounds.add(direct);
        return outbounds;
    }

    /**
     * 构建路由规则。
     *
     * @return routing 配置
     */
    private Map<String, Object> buildRouting() {
        Map<String, Object> routing = new LinkedHashMap<>();
        routing.put("domainStrategy", "AsIs");
        Map<String, Object> rule = new LinkedHashMap<>();
        rule.put("type", "field");
        rule.put("network", "tcp,udp");
        rule.put("outboundTag", "proxy");
        routing.put("rules", Collections.singletonList(rule));
        return routing;
    }

    /**
     * 构建代理 outbound。
     *
     * @param protocol 协议
     * @param proxyUrl 节点URL
     * @return outbound 配置
     */
    private Map<String, Object> buildProxyOutbound(String protocol, String proxyUrl) {
        switch (protocol) {
            case "ss":
                return buildSsOutbound(proxyUrl);
            case "vmess":
                return buildVmessOutbound(proxyUrl);
            case "vless":
                return buildVlessOutbound(proxyUrl);
            case "trojan":
                return buildTrojanOutbound(proxyUrl);
            case "hysteria2":
            case "hy2":
                return buildHysteria2Outbound(proxyUrl);
            default:
                throw new IllegalArgumentException("unsupported relay protocol: " + protocol);
        }
    }

    /**
     * 构建 ss outbound。
     *
     * @param proxyUrl 节点URL
     * @return outbound
     */
    private Map<String, Object> buildSsOutbound(String proxyUrl) {
        String raw = trimScheme(proxyUrl, "ss://");
        raw = dropFragmentAndQuery(raw);
        String authPart;
        String hostPort;
        if (raw.contains("@")) {
            String[] parts = raw.split("@", 2);
            authPart = parts[0];
            hostPort = parts[1];
        } else {
            String decoded = decodeBase64Segment(raw);
            int idx = decoded.lastIndexOf('@');
            if (idx <= 0) {
                throw new IllegalArgumentException("invalid ss url: missing host info");
            }
            authPart = decoded.substring(0, idx);
            hostPort = decoded.substring(idx + 1);
        }
        if (!authPart.contains(":")) {
            authPart = decodeBase64Segment(authPart);
        }
        int authIdx = authPart.indexOf(':');
        if (authIdx <= 0) {
            throw new IllegalArgumentException("invalid ss url: missing method/password");
        }
        String method = authPart.substring(0, authIdx);
        String password = authPart.substring(authIdx + 1);
        HostPort hp = parseHostPort(hostPort);

        Map<String, Object> server = new LinkedHashMap<>();
        server.put("address", hp.host);
        server.put("port", hp.port);
        server.put("method", method);
        server.put("password", password);

        Map<String, Object> settings = new LinkedHashMap<>();
        settings.put("servers", Collections.singletonList(server));

        Map<String, Object> outbound = new LinkedHashMap<>();
        outbound.put("tag", "proxy");
        outbound.put("protocol", "shadowsocks");
        outbound.put("settings", settings);
        return outbound;
    }

    /**
     * 构建 vmess outbound。
     *
     * @param proxyUrl 节点URL
     * @return outbound
     */
    private Map<String, Object> buildVmessOutbound(String proxyUrl) {
        String encoded = trimScheme(proxyUrl, "vmess://");
        String decoded = decodeBase64Segment(encoded);
        Map<String, Object> node = JacksonUtils.toMap(decoded, String.class, Object.class);
        if (node == null || node.isEmpty()) {
            throw new IllegalArgumentException("invalid vmess url");
        }
        String host = pickString(node, "add", "");
        int port = pickInt(node, "port", 0);
        String id = pickString(node, "id", "");
        int alterId = pickInt(node, "aid", 0);
        String security = pickString(node, "scy", "auto");
        String network = pickString(node, "net", "tcp");
        String tls = pickString(node, "tls", "");
        String sni = pickString(node, "sni", "");
        String wsHost = pickString(node, "host", "");
        String wsPath = pickString(node, "path", "");
        String type = pickString(node, "type", "");
        if (StringUtils.isBlank(host) || port <= 0 || StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("invalid vmess node fields");
        }

        Map<String, Object> user = new LinkedHashMap<>();
        user.put("id", id);
        user.put("alterId", alterId);
        user.put("security", security);

        Map<String, Object> vnext = new LinkedHashMap<>();
        vnext.put("address", host);
        vnext.put("port", port);
        vnext.put("users", Collections.singletonList(user));

        Map<String, Object> settings = new LinkedHashMap<>();
        settings.put("vnext", Collections.singletonList(vnext));

        Map<String, Object> outbound = new LinkedHashMap<>();
        outbound.put("tag", "proxy");
        outbound.put("protocol", "vmess");
        outbound.put("settings", settings);
        Map<String, Object> streamSettings = buildStreamSettings(network, tls, sni, wsHost, wsPath, type, "");
        if (!streamSettings.isEmpty()) {
            outbound.put("streamSettings", streamSettings);
        }
        return outbound;
    }

    /**
     * 构建 vless outbound。
     *
     * @param proxyUrl 节点URL
     * @return outbound
     */
    private Map<String, Object> buildVlessOutbound(String proxyUrl) {
        URI uri = safeUri(proxyUrl);
        String id = uri.getUserInfo();
        if (StringUtils.isBlank(id) || StringUtils.isBlank(uri.getHost()) || uri.getPort() <= 0) {
            throw new IllegalArgumentException("invalid vless url");
        }
        Map<String, String> query = parseQuery(uri.getRawQuery());
        String network = query.getOrDefault("type", "tcp");
        String security = query.getOrDefault("security", "");
        if (StringUtils.isBlank(security) && "reality".equalsIgnoreCase(query.getOrDefault("tls", ""))) {
            security = "reality";
        }
        String flow = query.getOrDefault("flow", "");
        if (StringUtils.isBlank(security) && StringUtils.isNotBlank(flow) && flow.toLowerCase(Locale.ROOT).contains("xtls-rprx")) {
            security = "reality";
        }
        String sni = firstNonBlank(query, "sni", "servername", "serverName");
        String wsHost = query.getOrDefault("host", "");
        String wsPath = query.getOrDefault("path", "");
        String headerType = firstNonBlank(query, "headerType", "header-type");
        String serviceName = query.getOrDefault("serviceName", "");
        String publicKey = firstNonBlank(query, "pbk", "public-key", "publicKey");
        String shortId = firstNonBlank(query, "sid", "short-id", "shortId");
        String fingerprint = firstNonBlank(query, "fp", "client-fingerprint", "fingerprint");

        Map<String, Object> user = new LinkedHashMap<>();
        user.put("id", id);
        user.put("encryption", query.getOrDefault("encryption", "none"));
        if (StringUtils.isNotBlank(flow)) {
            user.put("flow", flow);
        }

        Map<String, Object> vnext = new LinkedHashMap<>();
        vnext.put("address", uri.getHost());
        vnext.put("port", uri.getPort());
        vnext.put("users", Collections.singletonList(user));

        Map<String, Object> settings = new LinkedHashMap<>();
        settings.put("vnext", Collections.singletonList(vnext));

        Map<String, Object> outbound = new LinkedHashMap<>();
        outbound.put("tag", "proxy");
        outbound.put("protocol", "vless");
        outbound.put("settings", settings);
        if (StringUtils.isNotBlank(flow) && flow.toLowerCase(Locale.ROOT).contains("xtls-rprx")
                && !"reality".equalsIgnoreCase(security)) {
            throw new IllegalArgumentException("invalid vless config: xtls-rprx flow requires security=reality");
        }
        Map<String, Object> streamSettings = buildVlessStreamSettings(network, security, sni, wsHost, wsPath,
                headerType, serviceName, publicKey, shortId, fingerprint);
        if (!streamSettings.isEmpty()) {
            outbound.put("streamSettings", streamSettings);
        }
        return outbound;
    }

    /**
     * 构建 vless streamSettings，支持 reality 参数。
     *
     * @param network 传输层
     * @param security 安全层
     * @param sni SNI
     * @param wsHost ws host
     * @param wsPath ws path
     * @param headerType tcp 头类型
     * @param serviceName grpc serviceName
     * @param publicKey reality public key
     * @param shortId reality short id
     * @param fingerprint reality 指纹
     * @return streamSettings
     */
    private Map<String, Object> buildVlessStreamSettings(String network, String security, String sni,
                                                         String wsHost, String wsPath, String headerType, String serviceName,
                                                         String publicKey, String shortId, String fingerprint) {
        if (!"reality".equalsIgnoreCase(security)) {
            return buildStreamSettings(network, security, sni, wsHost, wsPath, headerType, serviceName);
        }
        if (StringUtils.isBlank(publicKey)) {
            throw new IllegalArgumentException("invalid vless reality config: public key required (query key: pbk/public-key)");
        }
        Map<String, Object> stream = new LinkedHashMap<>();
        String normalizedNetwork = StringUtils.isBlank(network) ? "tcp" : network.trim().toLowerCase(Locale.ROOT);
        stream.put("network", normalizedNetwork);
        stream.put("security", "reality");
        Map<String, Object> realitySettings = new LinkedHashMap<>();
        if (StringUtils.isNotBlank(sni)) {
            realitySettings.put("serverName", sni);
        }
        if (StringUtils.isNotBlank(publicKey)) {
            realitySettings.put("publicKey", publicKey);
        }
        if (StringUtils.isNotBlank(shortId)) {
            realitySettings.put("shortId", shortId);
        }
        if (StringUtils.isNotBlank(fingerprint)) {
            realitySettings.put("fingerprint", fingerprint);
        }
        if (!realitySettings.isEmpty()) {
            stream.put("realitySettings", realitySettings);
        }
        if ("ws".equals(normalizedNetwork)) {
            Map<String, Object> ws = new LinkedHashMap<>();
            ws.put("path", StringUtils.isBlank(wsPath) ? "/" : wsPath);
            if (StringUtils.isNotBlank(wsHost)) {
                ws.put("headers", Collections.singletonMap("Host", wsHost));
            }
            stream.put("wsSettings", ws);
        } else if ("grpc".equals(normalizedNetwork)) {
            Map<String, Object> grpc = new LinkedHashMap<>();
            if (StringUtils.isNotBlank(serviceName)) {
                grpc.put("serviceName", serviceName);
            }
            stream.put("grpcSettings", grpc);
        } else if ("tcp".equals(normalizedNetwork) && "http".equalsIgnoreCase(headerType)) {
            Map<String, Object> tcpSettings = new LinkedHashMap<>();
            tcpSettings.put("header", Collections.singletonMap("type", "http"));
            stream.put("tcpSettings", tcpSettings);
        }
        return stream;
    }

    /**
     * 构建 trojan outbound。
     *
     * @param proxyUrl 节点URL
     * @return outbound
     */
    private Map<String, Object> buildTrojanOutbound(String proxyUrl) {
        URI uri = safeUri(proxyUrl);
        String password = uri.getUserInfo();
        if (StringUtils.isBlank(password) || StringUtils.isBlank(uri.getHost()) || uri.getPort() <= 0) {
            throw new IllegalArgumentException("invalid trojan url");
        }
        Map<String, String> query = parseQuery(uri.getRawQuery());
        String network = query.getOrDefault("type", "tcp");
        String security = query.getOrDefault("security", "tls");
        String sni = query.getOrDefault("sni", "");
        String wsHost = query.getOrDefault("host", "");
        String wsPath = query.getOrDefault("path", "");
        String headerType = query.getOrDefault("headerType", "");
        String serviceName = query.getOrDefault("serviceName", "");

        Map<String, Object> server = new LinkedHashMap<>();
        server.put("address", uri.getHost());
        server.put("port", uri.getPort());
        server.put("password", password);

        Map<String, Object> settings = new LinkedHashMap<>();
        settings.put("servers", Collections.singletonList(server));

        Map<String, Object> outbound = new LinkedHashMap<>();
        outbound.put("tag", "proxy");
        outbound.put("protocol", "trojan");
        outbound.put("settings", settings);
        Map<String, Object> streamSettings = buildStreamSettings(network, security, sni, wsHost, wsPath, headerType, serviceName);
        if (!streamSettings.isEmpty()) {
            outbound.put("streamSettings", streamSettings);
        }
        return outbound;
    }

    /**
     * 构建 hysteria2 outbound。
     *
     * @param proxyUrl 节点URL
     * @return outbound
     */
    private Map<String, Object> buildHysteria2Outbound(String proxyUrl) {
        URI uri = safeUri(proxyUrl);
        String password = uri.getUserInfo();
        if (StringUtils.isBlank(password) || StringUtils.isBlank(uri.getHost()) || uri.getPort() <= 0) {
            throw new IllegalArgumentException("invalid hysteria2 url");
        }
        Map<String, String> query = parseQuery(uri.getRawQuery());
        String sni = query.getOrDefault("sni", "");
        String insecure = query.getOrDefault("insecure", query.getOrDefault("allowInsecure", ""));

        Map<String, Object> server = new LinkedHashMap<>();
        server.put("address", uri.getHost());
        server.put("port", uri.getPort());
        server.put("password", password);

        Map<String, Object> settings = new LinkedHashMap<>();
        settings.put("servers", Collections.singletonList(server));

        Map<String, Object> outbound = new LinkedHashMap<>();
        outbound.put("tag", "proxy");
        outbound.put("protocol", "hysteria2");
        outbound.put("settings", settings);

        Map<String, Object> streamSettings = new LinkedHashMap<>();
        streamSettings.put("security", "tls");
        Map<String, Object> tlsSettings = new LinkedHashMap<>();
        if (StringUtils.isNotBlank(sni)) {
            tlsSettings.put("serverName", sni);
        }
        if ("1".equals(insecure) || "true".equalsIgnoreCase(insecure)) {
            tlsSettings.put("allowInsecure", true);
        }
        if (!tlsSettings.isEmpty()) {
            streamSettings.put("tlsSettings", tlsSettings);
        }
        outbound.put("streamSettings", streamSettings);
        return outbound;
    }

    /**
     * 构建 streamSettings。
     *
     * @param network 传输层
     * @param security 安全层
     * @param sni SNI
     * @param wsHost ws host
     * @param wsPath ws path
     * @param headerType 头类型
     * @param serviceName grpc serviceName
     * @return streamSettings
     */
    private Map<String, Object> buildStreamSettings(String network, String security, String sni,
                                                    String wsHost, String wsPath, String headerType, String serviceName) {
        Map<String, Object> stream = new LinkedHashMap<>();
        String normalizedNetwork = StringUtils.isBlank(network) ? "tcp" : network.trim().toLowerCase(Locale.ROOT);
        stream.put("network", normalizedNetwork);
        if (StringUtils.isNotBlank(security)) {
            stream.put("security", security);
            if ("tls".equalsIgnoreCase(security) && StringUtils.isNotBlank(sni)) {
                Map<String, Object> tls = new LinkedHashMap<>();
                tls.put("serverName", sni);
                stream.put("tlsSettings", tls);
            }
        }
        if ("ws".equals(normalizedNetwork)) {
            Map<String, Object> ws = new LinkedHashMap<>();
            ws.put("path", StringUtils.isBlank(wsPath) ? "/" : wsPath);
            if (StringUtils.isNotBlank(wsHost)) {
                ws.put("headers", Collections.singletonMap("Host", wsHost));
            }
            stream.put("wsSettings", ws);
        } else if ("grpc".equals(normalizedNetwork)) {
            Map<String, Object> grpc = new LinkedHashMap<>();
            if (StringUtils.isNotBlank(serviceName)) {
                grpc.put("serviceName", serviceName);
            }
            stream.put("grpcSettings", grpc);
        } else if ("tcp".equals(normalizedNetwork) && "http".equalsIgnoreCase(headerType)) {
            Map<String, Object> tcpSettings = new LinkedHashMap<>();
            tcpSettings.put("header", Collections.singletonMap("type", "http"));
            stream.put("tcpSettings", tcpSettings);
        }
        return stream;
    }

    /**
     * 启动 xray 进程。
     *
     * @param configPath 配置文件
     * @param logPath 日志文件
     * @return 进程对象
     */
    private Process startProcess(Path configPath, Path logPath) {
        String xrayBin = resolveXrayBin();
        ProcessBuilder processBuilder = new ProcessBuilder(xrayBin, "run", "-c", configPath.toString());
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(logPath.toFile()));
        try {
            return processBuilder.start();
        } catch (IOException ex) {
            throw new IllegalStateException("start relay process failed, xrayBin=" + xrayBin + ", error=" + ex.getMessage(), ex);
        }
    }

    /**
     * 等待中继端口启动。
     *
     * @param localPort 本地端口
     * @param process 进程
     * @param relayKey 中继键
     * @param logPath 日志文件
     */
    private void waitRelayStarted(int localPort, Process process, String relayKey, Path logPath,
                                  String protocol, String nodeSummary) {
        long deadline = System.currentTimeMillis() + RELAY_STARTUP_TIMEOUT_MS;
        while (System.currentTimeMillis() < deadline) {
            if (!process.isAlive()) {
                String detail = readRelayLogTail(logPath, 40);
                if (detail.contains("unknown config id: hysteria2")) {
                    throw new IllegalStateException("relay exited early: current xray build does not support hysteria2, relayKey="
                            + relayKey + ", protocol=" + protocol + ", node=" + nodeSummary + ", log=" + logPath);
                }
                if ("vless".equals(protocol) && detail.contains("REALITY")) {
                    throw new IllegalStateException("relay exited early: vless reality config invalid, relayKey="
                            + relayKey + ", protocol=" + protocol + ", node=" + nodeSummary + ", log=" + logPath
                            + ", detail=" + detail);
                }
                throw new IllegalStateException("relay exited early, relayKey=" + relayKey + ", log=" + logPath
                        + ", protocol=" + protocol + ", node=" + nodeSummary + ", detail=" + detail);
            }
            if (isPortOpen(LOCAL_RELAY_HOST, localPort)) {
                return;
            }
            sleepQuietly(100L);
        }
        process.destroyForcibly();
        throw new IllegalStateException("relay startup timeout, relayKey=" + relayKey + ", protocol="
                + protocol + ", node=" + nodeSummary + ", log=" + logPath);
    }

    /**
     * 探测端口是否可连。
     *
     * @param host 主机
     * @param port 端口
     * @return true 可用
     */
    private boolean isPortOpen(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), RELAY_PORT_PROBE_TIMEOUT_MS);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 写入配置文件。
     *
     * @param configPath 配置文件路径
     * @param config 配置对象
     */
    private void writeConfig(Path configPath, Map<String, Object> config) {
        try {
            Files.writeString(configPath, JacksonUtils.toJson(config), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            throw new IllegalStateException("write relay config failed: " + configPath, ex);
        }
    }

    /**
     * 确保工作目录存在。
     *
     * @return 工作目录
     */
    private Path ensureWorkDir() {
        String custom = System.getProperty(KEY_WORK_DIR_SYS);
        if (StringUtils.isBlank(custom)) {
            custom = System.getenv(KEY_WORK_DIR_ENV);
        }
        Path workDir = Path.of(StringUtils.isBlank(custom) ? DEFAULT_WORK_DIR : custom);
        try {
            Files.createDirectories(workDir);
            return workDir;
        } catch (IOException ex) {
            throw new IllegalStateException("create relay work dir failed: " + workDir, ex);
        }
    }

    /**
     * 解析 xray 二进制路径。
     *
     * @return xray 可执行路径
     */
    private String resolveXrayBin() {
        String custom = System.getProperty(KEY_XRAY_BIN_SYS);
        if (StringUtils.isBlank(custom)) {
            custom = System.getenv(KEY_XRAY_BIN_ENV);
        }
        return StringUtils.isBlank(custom) ? DEFAULT_XRAY_BIN : custom.trim();
    }

    /**
     * 分配本地临时端口。
     *
     * @return 可用端口
     */
    private int allocatePort() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        } catch (IOException ex) {
            throw new IllegalStateException("allocate relay local port failed", ex);
        }
    }

    /**
     * 解析 URI。
     *
     * @param value URI字符串
     * @return URI
     */
    private URI safeUri(String value) {
        try {
            return new URI(value);
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("invalid proxy url: " + value, ex);
        }
    }

    /**
     * 解析查询参数。
     *
     * @param rawQuery 原始query
     * @return 参数Map
     */
    private Map<String, String> parseQuery(String rawQuery) {
        if (StringUtils.isBlank(rawQuery)) {
            return Collections.emptyMap();
        }
        Map<String, String> map = new HashMap<>();
        String[] pairs = rawQuery.split("&");
        for (String pair : pairs) {
            if (StringUtils.isBlank(pair)) {
                continue;
            }
            String[] kv = pair.split("=", 2);
            if (kv.length == 1) {
                map.put(kv[0], "");
            } else {
                map.put(kv[0], decodeUrl(kv[1]));
            }
        }
        return map;
    }

    /**
     * 解析主机端口。
     *
     * @param hostPort host:port
     * @return 主机端口
     */
    private HostPort parseHostPort(String hostPort) {
        int idx = hostPort.lastIndexOf(':');
        if (idx <= 0 || idx + 1 >= hostPort.length()) {
            throw new IllegalArgumentException("invalid host:port -> " + hostPort);
        }
        String host = hostPort.substring(0, idx);
        int port = Integer.parseInt(hostPort.substring(idx + 1));
        return new HostPort(host, port);
    }

    /**
     * 去除协议前缀。
     *
     * @param value 原始值
     * @param scheme 协议前缀
     * @return 去除后的内容
     */
    private String trimScheme(String value, String scheme) {
        if (StringUtils.isBlank(value) || !value.startsWith(scheme)) {
            throw new IllegalArgumentException("invalid url, expected scheme " + scheme);
        }
        return value.substring(scheme.length());
    }

    /**
     * 去除 fragment 与 query。
     *
     * @param value 原始值
     * @return 净化后的内容
     */
    private String dropFragmentAndQuery(String value) {
        int idx = value.indexOf('#');
        if (idx >= 0) {
            value = value.substring(0, idx);
        }
        idx = value.indexOf('?');
        if (idx >= 0) {
            value = value.substring(0, idx);
        }
        return value;
    }

    /**
     * Base64 片段解码。
     *
     * @param value 编码字符串
     * @return 解码结果
     */
    private String decodeBase64Segment(String value) {
        String normalized = value.trim().replace('-', '+').replace('_', '/');
        int mod = normalized.length() % 4;
        if (mod > 0) {
            normalized = normalized + "====".substring(mod);
        }
        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(normalized);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("invalid base64 segment", ex);
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * URL decode。
     *
     * @param value 值
     * @return 解码值
     */
    private String decodeUrl(String value) {
        return value.replace("%2F", "/").replace("%3A", ":").replace("%40", "@")
                .replace("%3D", "=").replace("%3F", "?").replace("%26", "&").replace("%25", "%");
    }

    /**
     * 读取日志尾部内容。
     *
     * @param logPath 日志文件
     * @param maxLines 最大行数
     * @return 尾部日志
     */
    private String readRelayLogTail(Path logPath, int maxLines) {
        try {
            List<String> lines = Files.readAllLines(logPath);
            if (lines.isEmpty()) {
                return "";
            }
            int from = Math.max(0, lines.size() - Math.max(maxLines, 1));
            return String.join(" | ", lines.subList(from, lines.size()));
        } catch (Exception ex) {
            return "read relay log failed: " + ex.getMessage();
        }
    }

    /**
     * 取字符串值。
     *
     * @param map 数据源
     * @param key 字段
     * @param fallback 默认值
     * @return 字符串
     */
    private String pickString(Map<String, Object> map, String key, String fallback) {
        Object val = map.get(key);
        return val == null ? fallback : String.valueOf(val);
    }

    /**
     * 取整型值。
     *
     * @param map 数据源
     * @param key 字段
     * @param fallback 默认值
     * @return 整型
     */
    private int pickInt(Map<String, Object> map, String key, int fallback) {
        Object val = map.get(key);
        if (val == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(String.valueOf(val));
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    /**
     * 读取第一个非空 query 值。
     *
     * @param query query map
     * @param keys key 列表
     * @return 命中值
     */
    private String firstNonBlank(Map<String, String> query, String... keys) {
        if (query == null || keys == null) {
            return "";
        }
        for (String key : keys) {
            String value = query.get(key);
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return "";
    }

    /**
     * 构建中继节点摘要，避免日志中只有 hash key 不可读。
     *
     * @param protocol 协议
     * @param proxyUrl 节点 URL
     * @return 节点摘要
     */
    private String summarizeRelayNode(String protocol, String proxyUrl) {
        try {
            if ("vmess".equals(protocol)) {
                String encoded = trimScheme(proxyUrl, "vmess://");
                String decoded = decodeBase64Segment(encoded);
                Map<String, Object> node = JacksonUtils.toMap(decoded, String.class, Object.class);
                String host = pickString(node, "add", "");
                String port = String.valueOf(pickInt(node, "port", 0));
                return "{protocol=vmess,host=" + host + ",port=" + port + "}";
            }
            if ("ss".equals(protocol) || "vless".equals(protocol) || "trojan".equals(protocol)
                    || "hysteria2".equals(protocol) || "hy2".equals(protocol)) {
                URI uri = safeUri(proxyUrl);
                return "{protocol=" + protocol + ",host=" + uri.getHost() + ",port=" + uri.getPort() + "}";
            }
            return "{protocol=" + protocol + ",proxyUrl=" + proxyUrl + "}";
        } catch (Exception ex) {
            return "{protocol=" + protocol + ",proxyUrl=" + proxyUrl + ",summaryError=" + ex.getMessage() + "}";
        }
    }

    /**
     * 标准化协议名。
     *
     * @param protocol 原始协议
     * @return 标准化协议
     */
    private String normalizeProtocol(String protocol) {
        if (StringUtils.isBlank(protocol)) {
            return "";
        }
        return protocol.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * 计算 SHA-256。
     *
     * @param value 原始值
     * @return 十六进制摘要
     */
    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] result = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("sha256 failed", ex);
        }
    }

    /**
     * 安静休眠。
     *
     * @param millis 毫秒
     */
    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 主机端口结构。
     */
    private static final class HostPort {
        private final String host;
        private final int port;

        /**
         * 创建主机端口对象。
         *
         * @param host 主机
         * @param port 端口
         */
        private HostPort(String host, int port) {
            this.host = host;
            this.port = port;
        }
    }

    /**
     * 中继进程句柄。
     */
    private static final class RelayProcessHolder {
        private final Process process;
        private final int port;

        /**
         * 创建中继进程句柄。
         *
         * @param process 进程
         * @param port 本地端口
         */
        private RelayProcessHolder(Process process, int port) {
            this.process = process;
            this.port = port;
        }

        /**
         * 检查进程是否存活。
         *
         * @return true 存活
         */
        private boolean isAlive() {
            return process != null && process.isAlive();
        }

        /**
         * 读取本地端口。
         *
         * @return 端口
         */
        private int getPort() {
            return port;
        }
    }
}
