package work.soho.common.core.util;

import lombok.experimental.UtilityClass;

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
}
