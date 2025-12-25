package work.soho.common.core.util;

import lombok.experimental.UtilityClass;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@UtilityClass
public class IpUtils {
        /**
         * 获取客户端IP
         *
         * @return IP地址
         */
        public static String getClientIp()
        {
            HttpServletRequest request = RequestUtil.getRequest();
            if (request == null)
            {
                return null;
            }
            String ip = request.getHeader("x-forwarded-for");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            {
                ip = request.getHeader("X-Forwarded-For");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            {
                ip = request.getHeader("X-Real-IP");
            }

            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            {
                ip = request.getRemoteAddr();
            }

            ip = "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
            return ip.split(",")[0];
        }

    /**
     * 获取最大的long ip
     *
     * @return
     */
    public long maxLongIp() {
        List<String> ips = getLocalIPList();
        List<BigInteger> list = new ArrayList<>();
        for (String ip:ips
        ) {
            list.add(BigInteger.valueOf(ipToLong(ip)));
        }
        Collections.sort(list);
        if(!list.isEmpty()) {
            return list.get(list.size()-1).longValue();
        }
        return 0;
    }

    /**
     * ip to long
     *
     * @param strIp
     * @return
     */
    public long ipToLong(String strIp) {
        String[]ip = strIp.split("\\.");
        return (Long.parseLong(ip[0]) << 24) + (Long.parseLong(ip[1]) << 16) + (Long.parseLong(ip[2]) << 8) + Long.parseLong(ip[3]);
    }

    /**
     * long to ip
     *
     * @param longIp
     * @return
     */
    public String longToIP(long longIp) {
        StringBuilder sb = new StringBuilder();
        // 直接右移24位
        sb.append(String.valueOf((longIp >>> 24)));
        sb.append(".");
        // 将高8位置0，然后右移16位
        sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
        sb.append(".");
        // 将高16位置0，然后右移8位
        sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
        sb.append(".");
        // 将高24位置0
        sb.append(String.valueOf((longIp & 0x000000FF)));
        return sb.toString();
    }

    /**
     * 获取本地所有的ipv4地址
     *
     * @return
     */
    public static List<String> getLocalIPList() {
        List<String> ipList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface;
            Enumeration<InetAddress> inetAddresses;
            InetAddress inetAddress;
            String ip;
            while (networkInterfaces.hasMoreElements()) {
                networkInterface = networkInterfaces.nextElement();
                inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    inetAddress = inetAddresses.nextElement();
                    if (inetAddress instanceof Inet4Address) { // IPV4
                        ip = inetAddress.getHostAddress();
                        ipList.add(ip);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipList;
    }

    /**
     * 检查该IP是否在该子网内
     *
     * example:
     * <pre>
     *     subnet:  192.168.0.5/24
     *     checkIp: 192.168.0.6
     * </pre>
     *
     * @param subnet
     * @param checkIp
     * @return
     */
    public static boolean inSubnet(String subnet, String checkIp) {
        String[] parts = subnet.split("/");
        Long longIp = IpUtils.ipToLong(checkIp);
        Long matchIp = IpUtils.ipToLong(parts[0]);
        Integer matchLen = parts.length<2 ? 32 : Integer.parseInt(parts[1]);
        Integer outLen = 32 - matchLen;
        return (longIp>>outLen) == (matchIp>>outLen);
    }
}
