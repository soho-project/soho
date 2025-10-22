package work.soho.common.data.ip2region;

import lombok.extern.log4j.Log4j2;
import org.lionsoul.ip2region.xdb.Searcher;
import org.lionsoul.ip2region.xdb.Version;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Log4j2
public class Ip2regionUtil {

    private static final String V4_REGION_NAME = "ip2region_v4.xdb";
    private static final String V6_REGION_NAME = "ip2region_v6.xdb";

    // 缓存搜索器实例，避免重复创建
    private static final ConcurrentMap<String, Searcher> SEARCHER_CACHE = new ConcurrentHashMap<>();

    // 搜索器类型常量
    private static final String SEARCHER_V4 = "v4";
    private static final String SEARCHER_V6 = "v6";

    // 未知区域标识
    private static final String UNKNOWN_REGION_FLAG = "0";

    /**
     * 私有构造方法，防止实例化
     */
    private Ip2regionUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * 获取搜索器实例
     *
     * @param searcherType 搜索器类型
     * @return Searcher实例
     */
    private static Searcher getSearcher(String searcherType) {
        return SEARCHER_CACHE.computeIfAbsent(searcherType, type -> {
            String dbFileName = SEARCHER_V4.equals(type) ? V4_REGION_NAME : V6_REGION_NAME;
            Version version = SEARCHER_V4.equals(type) ? Version.IPv4 : Version.IPv6;

            String dbPath = getDatabasePath(dbFileName);
            if (dbPath == null) {
                log.error("Failed to find database file: {}", dbFileName);
                return null;
            }

            try {
                Searcher searcher = Searcher.newWithFileOnly(version, dbPath);
                log.info("Successfully loaded {} searcher", type);
                return searcher;
            } catch (IOException e) {
                log.error("Failed to create searcher with '{}': {}", dbPath, e.getMessage());
                return null;
            }
        });
    }

    /**
     * 获取数据库文件路径
     *
     * @param fileName 文件名
     * @return 文件路径
     */
    private static String getDatabasePath(String fileName) {
        try {
            return Objects.requireNonNull(
                    Ip2regionUtil.class.getClassLoader().getResource(fileName)
            ).getPath();
        } catch (NullPointerException e) {
            log.error("Failed to find {} in resources", fileName);
            return null;
        }
    }

    /**
     * 根据IP获取城市信息
     *
     * @param ip IP地址
     * @return 城市信息字符串
     */
    public static String getCityInfo(String ip) {
        if (!isValidIp(ip)) {
            log.warn("Invalid IP address: {}", ip);
            return null;
        }

        String ipVersion = getIPVersion(ip);
        Searcher searcher = null;

        try {
            if ("IPv6".equals(ipVersion)) {
                searcher = getSearcher(SEARCHER_V6);
            } else if ("IPv4".equals(ipVersion)) {
                searcher = getSearcher(SEARCHER_V4);
            } else {
                log.warn("Unsupported IP version for address: {}", ip);
                return null;
            }

            if (searcher == null) {
                log.error("Searcher not available for IP: {}", ip);
                return null;
            }

            return searcher.search(ip);
        } catch (Exception e) {
            log.error("Failed to search IP {}: {}", ip, e.getMessage());
            return null;
        }
        // 注意：这里不再关闭searcher，因为我们在缓存中重复使用它
    }

    /**
     * 根据IP获取区域信息对象
     *
     * @param ip IP地址
     * @return RegionInfo对象
     */
    public static RegionInfo getRegionInfoByIp(String ip) {
        String cityInfo = getCityInfo(ip);
        if (cityInfo == null || cityInfo.trim().isEmpty()) {
            log.debug("No city info found for IP: {}", ip);
            return createEmptyRegionInfo();
        }

        try {
            String[] parts = cityInfo.split("\\|");
            if (parts.length < 4) {
                log.warn("Invalid city info format for IP {}: {}", ip, cityInfo);
                return createEmptyRegionInfo();
            }

            RegionInfo regionInfo = new RegionInfo();
            regionInfo.setCountry(parseRegionField(parts[0]));
            regionInfo.setProvince(parseRegionField(parts[1]));
            regionInfo.setCity(parseRegionField(parts[2]));
            regionInfo.setIsp(parseRegionField(parts[3]));
            return regionInfo;
        } catch (Exception e) {
            log.error("Failed to parse region info for IP {}: {}", ip, e.getMessage());
            return createEmptyRegionInfo();
        }
    }

    /**
     * 解析区域字段，处理未知标识
     *
     * @param field 字段值
     * @return 处理后的字段值
     */
    private static String parseRegionField(String field) {
        return UNKNOWN_REGION_FLAG.equals(field) ? "" : field;
    }

    /**
     * 创建空的区域信息对象
     *
     * @return 空的RegionInfo对象
     */
    private static RegionInfo createEmptyRegionInfo() {
        RegionInfo regionInfo = new RegionInfo();
        regionInfo.setCountry("");
        regionInfo.setProvince("");
        regionInfo.setCity("");
        regionInfo.setIsp("");
        return regionInfo;
    }

    /**
     * 获取IP版本
     *
     * @param ipAddress IP地址
     * @return IP版本 (IPv4/IPv6/Invalid IP Address)
     */
    private static String getIPVersion(String ipAddress) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            return inetAddress.getAddress().length == 4 ? "IPv4" : "IPv6";
        } catch (UnknownHostException e) {
            log.debug("Invalid IP address: {}", ipAddress);
            return "Invalid IP Address";
        }
    }

    /**
     * 验证IP地址格式
     *
     * @param ip IP地址
     * @return 是否有效
     */
    private static boolean isValidIp(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return false;
        }

        String ipVersion = getIPVersion(ip);
        return "IPv4".equals(ipVersion) || "IPv6".equals(ipVersion);
    }

    /**
     * 清理资源
     * 在应用程序关闭时调用此方法
     */
    public static void close() {
        SEARCHER_CACHE.forEach((type, searcher) -> {
            if (searcher != null) {
                try {
                    searcher.close();
                    log.info("Closed {} searcher successfully", type);
                } catch (Exception e) {
                    log.error("Failed to close {} searcher: {}", type, e.getMessage());
                }
            }
        });
        SEARCHER_CACHE.clear();
        log.info("All IP searchers closed");
    }

    /**
     * 获取缓存状态（用于监控）
     *
     * @return 缓存状态信息
     */
    public static String getCacheStatus() {
        return String.format("Searcher cache: IPv4=%s, IPv6=%s",
                SEARCHER_CACHE.containsKey(SEARCHER_V4),
                SEARCHER_CACHE.containsKey(SEARCHER_V6));
    }
}