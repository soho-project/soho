package work.soho.pay.biz.platform.ndpay.adapter;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class NdpaySignUtil {
    private NdpaySignUtil() {
    }

    public static String sign(Map<String, ?> params, String key) {
        if(StrUtil.isBlank(key)) {
            throw new IllegalArgumentException("ndpay apiKey 不能为空");
        }
        String signText = buildSignText(params) + "&key=" + key;
        return SecureUtil.md5(signText).toUpperCase();
    }

    public static boolean verify(Map<String, ?> params, String key, String sign) {
        if(StrUtil.isBlank(sign)) {
            return false;
        }
        return StrUtil.equalsIgnoreCase(sign(params, key), sign);
    }

    private static String buildSignText(Map<String, ?> params) {
        TreeMap<String, String> sorted = new TreeMap<>();
        params.forEach((k, v) -> {
            if("sign".equals(k) || v == null) {
                return;
            }
            String value = String.valueOf(v);
            if(StrUtil.isBlank(value)) {
                return;
            }
            sorted.put(k, value);
        });
        return sorted.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
    }
}
