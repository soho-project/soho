package work.soho.ai.biz.utils;

import work.soho.common.core.util.StringUtils;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Map;

/**
 * AI 模块统一代理层工具，负责解析配置并构建代理对象。
 */
public final class AiProxyLayerUtils {

    private AiProxyLayerUtils() {
    }

    /**
     * 将配置解析成代理设置。
     *
     * @param config 提供方配置JSON解析后的Map
     * @return 代理设置；未配置代理时返回null
     */
    public static ProxySettings resolve(Map<String, Object> config) {
        if (config == null || config.isEmpty()) {
            return null;
        }
        ProxySettings byUrl = resolveByProxyUrl(config);
        if (byUrl != null) {
            return byUrl;
        }
        return resolveByDiscreteFields(config);
    }

    /**
     * 构建 Java RestTemplate 使用的代理对象。
     *
     * @param config 提供方配置JSON解析后的Map
     * @return Java代理对象；未配置代理时返回null
     */
    public static Proxy buildJavaProxy(Map<String, Object> config) {
        ProxySettings settings = resolve(config);
        return buildJavaProxy(settings);
    }

    /**
     * 基于代理设置构建 Java RestTemplate 使用的代理对象。
     *
     * @param settings 代理设置
     * @return Java代理对象；未配置代理时返回null
     */
    public static Proxy buildJavaProxy(ProxySettings settings) {
        if (settings == null) {
            return null;
        }
        return new Proxy(settings.getJavaProxyType(),
                InetSocketAddress.createUnresolved(settings.getHost(), settings.getPort()));
    }

    /**
     * 从 proxyUrl 解析代理配置。
     *
     * @param config 提供方配置JSON解析后的Map
     * @return 代理设置；未配置 proxyUrl 时返回null
     */
    private static ProxySettings resolveByProxyUrl(Map<String, Object> config) {
        String proxyUrl = pickString(config, "proxyUrl");
        if (StringUtils.isBlank(proxyUrl)) {
            return null;
        }
        String value = proxyUrl.trim();
        int splitIdx = value.indexOf("://");
        if (splitIdx <= 0) {
            throw new IllegalArgumentException("proxyUrl format invalid, expected protocol://host:port");
        }
        String protocol = value.substring(0, splitIdx).toLowerCase(Locale.ROOT);
        ProtocolType protocolType = mapProtocol(protocol);
        URI uri = parseUri(value, protocol);
        String host = uri.getHost();
        int port = uri.getPort();
        if (StringUtils.isBlank(host) || port <= 0) {
            if (protocolType.isLocalRelayRequired()) {
                String fallbackHost = firstNonBlank(
                        pickString(config, "proxyHost"),
                        pickString(config, "hostname"),
                        pickString(config, "host"),
                        "127.0.0.1");
                Integer fallbackPort = pickInteger(config, "proxyPort");
                if (fallbackPort == null) {
                    fallbackPort = pickInteger(config, "port");
                }
                int safePort = fallbackPort == null ? 1 : Math.max(fallbackPort, 1);
                host = fallbackHost;
                port = safePort;
            } else {
                throw new IllegalArgumentException("proxyUrl format invalid, expected protocol://host:port");
            }
        }
        String username = pickString(config, "proxyUsername");
        String password = pickString(config, "proxyPassword");
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            String[] userInfo = parseUserInfo(uri.getUserInfo());
            if (StringUtils.isBlank(username)) {
                username = userInfo[0];
            }
            if (StringUtils.isBlank(password)) {
                password = userInfo[1];
            }
        }
        return new ProxySettings(protocolType, host, port, username, password, protocolType.isLocalRelayRequired(), protocol, value);
    }

    /**
     * 从 proxyType/proxyHost/proxyPort 字段解析代理配置。
     *
     * @param config 提供方配置JSON解析后的Map
     * @return 代理设置；未配置完整参数时返回null
     */
    private static ProxySettings resolveByDiscreteFields(Map<String, Object> config) {
        String protocol = pickString(config, "proxyType");
        if (StringUtils.isBlank(protocol)) {
            protocol = pickString(config, "proxyProtocol");
        }
        if (StringUtils.isBlank(protocol)) {
            return null;
        }
        String host = firstNonBlank(
                pickString(config, "proxyHost"),
                pickString(config, "hostname"),
                pickString(config, "host"));
        Integer port = pickInteger(config, "proxyPort");
        if (port == null) {
            port = pickInteger(config, "port");
        }
        if (StringUtils.isBlank(host) || port == null || port <= 0) {
            return null;
        }
        String username = firstNonBlank(
                pickString(config, "proxyUsername"),
                pickString(config, "proxyUser"),
                pickString(config, "username"));
        String password = firstNonBlank(
                pickString(config, "proxyPassword"),
                pickString(config, "proxyPass"),
                pickString(config, "password"));
        ProtocolType protocolType = mapProtocol(protocol);
        String normalizedProtocol = StringUtils.isBlank(protocol) ? null : protocol.trim().toLowerCase(Locale.ROOT);
        return new ProxySettings(protocolType, host.trim(), port, username, password, protocolType.isLocalRelayRequired(),
                normalizedProtocol, null);
    }

    /**
     * 映射协议类型。
     *
     * @param protocol 协议名称
     * @return 规范化协议类型
     */
    private static ProtocolType mapProtocol(String protocol) {
        String value = StringUtils.isBlank(protocol) ? "" : protocol.trim().toLowerCase(Locale.ROOT);
        switch (value) {
            case "http":
            case "https":
                return ProtocolType.HTTP;
            case "socks":
            case "socks5":
                return ProtocolType.SOCKS5;
            case "ss":
            case "vmess":
            case "vless":
            case "trojan":
            case "hysteria2":
            case "hy2":
                return ProtocolType.SOCKS5_LOCAL_RELAY;
            default:
                throw new IllegalArgumentException("unsupported proxyType: " + protocol);
        }
    }

    /**
     * 安全解析 URI，避免协议别名导致异常难读。
     *
     * @param value 原始URL
     * @param protocol 协议名
     * @return 解析后的URI
     */
    private static URI parseUri(String value, String protocol) {
        try {
            return new URI(value);
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("proxyUrl parse failed for protocol: " + protocol, ex);
        }
    }

    /**
     * 解析 userInfo 用户信息。
     *
     * @param userInfo URI userInfo
     * @return 用户名密码数组，长度为2
     */
    private static String[] parseUserInfo(String userInfo) {
        if (StringUtils.isBlank(userInfo)) {
            return new String[]{null, null};
        }
        int idx = userInfo.indexOf(':');
        if (idx < 0) {
            return new String[]{userInfo, null};
        }
        return new String[]{
                userInfo.substring(0, idx),
                idx + 1 >= userInfo.length() ? null : userInfo.substring(idx + 1)
        };
    }

    /**
     * 读取字符串配置项。
     *
     * @param config 配置Map
     * @param key 字段名
     * @return 字符串值
     */
    private static String pickString(Map<String, Object> config, String key) {
        Object value = config.get(key);
        return value == null ? null : String.valueOf(value);
    }

    /**
     * 读取整型配置项。
     *
     * @param config 配置Map
     * @param key 字段名
     * @return 整型值
     */
    private static Integer pickInteger(Map<String, Object> config, String key) {
        Object value = config.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        String str = String.valueOf(value).trim();
        if (StringUtils.isBlank(str)) {
            return null;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("invalid integer value for " + key + ": " + str, ex);
        }
    }

    /**
     * 返回第一个非空字符串。
     *
     * @param values 待选值
     * @return 非空字符串；都为空时返回null
     */
    private static String firstNonBlank(String... values) {
        if (values == null || values.length == 0) {
            return null;
        }
        for (String value : values) {
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 协议类型定义。
     */
    private enum ProtocolType {
        HTTP,
        SOCKS5,
        SOCKS5_LOCAL_RELAY;

        /**
         * 转 Java 代理类型。
         *
         * @return Java代理类型
         */
        Proxy.Type getJavaType() {
            return this == HTTP ? Proxy.Type.HTTP : Proxy.Type.SOCKS;
        }

        /**
         * 是否属于需要本地中继出口的协议。
         *
         * @return true表示需要本地中继
         */
        boolean isLocalRelayRequired() {
            return this == SOCKS5_LOCAL_RELAY;
        }
    }

    /**
     * 解析后的代理配置。
     */
    public static final class ProxySettings {
        private final ProtocolType protocolType;
        private final String host;
        private final int port;
        private final String username;
        private final String password;
        private final boolean localRelayRequired;
        private final String protocol;
        private final String proxyUrl;

        /**
         * 创建代理配置对象。
         *
         * @param protocolType 协议类型
         * @param host 代理主机
         * @param port 代理端口
         * @param username 用户名
         * @param password 密码
         * @param localRelayRequired 是否要求本地中继
         */
        public ProxySettings(ProtocolType protocolType, String host, int port, String username, String password, boolean localRelayRequired) {
            this(protocolType, host, port, username, password, localRelayRequired, null, null);
        }

        /**
         * 创建代理配置对象。
         *
         * @param protocolType 协议类型
         * @param host 代理主机
         * @param port 代理端口
         * @param username 用户名
         * @param password 密码
         * @param localRelayRequired 是否要求本地中继
         * @param protocol 原始协议
         * @param proxyUrl 原始代理URL
         */
        public ProxySettings(ProtocolType protocolType, String host, int port, String username, String password,
                             boolean localRelayRequired, String protocol, String proxyUrl) {
            this.protocolType = protocolType;
            this.host = host;
            this.port = port;
            this.username = username;
            this.password = password;
            this.localRelayRequired = localRelayRequired;
            this.protocol = protocol;
            this.proxyUrl = proxyUrl;
        }

        /**
         * 读取 Java 代理类型。
         *
         * @return Java代理类型
         */
        public Proxy.Type getJavaProxyType() {
            return protocolType.getJavaType();
        }

        /**
         * 读取代理主机。
         *
         * @return 主机
         */
        public String getHost() {
            return host;
        }

        /**
         * 读取代理端口。
         *
         * @return 端口
         */
        public int getPort() {
            return port;
        }

        /**
         * 读取代理用户名。
         *
         * @return 用户名
         */
        public String getUsername() {
            return username;
        }

        /**
         * 读取代理密码。
         *
         * @return 密码
         */
        public String getPassword() {
            return password;
        }

        /**
         * 是否需要本地中继。
         *
         * @return true表示协议需要本地中继
         */
        public boolean isLocalRelayRequired() {
            return localRelayRequired;
        }

        /**
         * 是否为 HTTP 代理。
         *
         * @return true表示HTTP
         */
        public boolean isHttpProxy() {
            return protocolType == ProtocolType.HTTP;
        }

        /**
         * 读取原始协议。
         *
         * @return 原始协议
         */
        public String getProtocol() {
            return protocol;
        }

        /**
         * 读取原始代理URL。
         *
         * @return 原始代理URL
         */
        public String getProxyUrl() {
            return proxyUrl;
        }
    }
}
