package work.soho.common.data.ip2region;

import lombok.extern.log4j.Log4j2;
import org.lionsoul.ip2region.xdb.Searcher;

import java.io.IOException;
import java.util.Objects;

@Log4j2
public class Ip2regionUtil {

    /**
     * 初始化
     *
     * @return
     */
    private static Searcher getSearcher() {
        Searcher searcher = null;
        String dbPath = null;
        try {
            dbPath = Objects.requireNonNull(
                    Ip2regionUtil.class.getClassLoader().getResource("ip2region.xdb")
            ).getPath();
        } catch (NullPointerException e) {
            log.error("failed to find ip2region.xdb in resources: {}\n", e);
            return null;
        }

        try {
            searcher = Searcher.newWithFileOnly(dbPath);
        } catch (IOException e) {
            log.error("failed to create searcher with `{}`: {}\n", dbPath, e);
            return null;
        }
        return searcher;
    }

    /**
     * 根据ip获取城市信息
     *
     * @param ip
     * @return
     */
    public static String getCityInfo(String ip) {
        Searcher searcher = null;
        try {
            searcher = getSearcher();
            return Objects.requireNonNull(searcher).search(ip);
        } catch (Exception e) {
            log.error("failed to search({}): {}\n", ip, e);
        } finally {
            if (searcher != null) {
                try {
                    searcher.close();
                } catch (Exception e) {
                    log.error("failed to close: {}\n", e);
                }
            }
        }
        return null;
    }

    /**
     * 根据ip获取城市信息
     *
     * @param ip
     * @return
     */
    public static RegionInfo getRegionInfoByIp(String ip) {
        String cityInfo = getCityInfo(ip);
        String[] parts = cityInfo.split("\\|");
        RegionInfo regionInfo = new RegionInfo();
        regionInfo.setCountry(parts[0].equals("0") ? "" : parts[0]);
        regionInfo.setProvince(parts[2].equals("0") ? "" : parts[2]);
        regionInfo.setCity(parts[3].equals("0") ? "" : parts[3]);
        regionInfo.setIsp(parts[4].equals("0") ? "" : parts[4]);
        return regionInfo;
    }
}
