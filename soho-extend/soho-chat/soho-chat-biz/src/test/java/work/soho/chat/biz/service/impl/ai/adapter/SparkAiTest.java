package work.soho.chat.biz.service.impl.ai.adapter;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.test.TestApp;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("local")
class SparkAiTest {

    public static String encodeURL(Map<String, String> map) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(URLEncoder.encode(entry.getKey(), "UTF-8")).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8"));
//            if (!entry.equals(map.entrySet().last())) {
//                sb.append("&");
//            }
            sb.append("&");
        }
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '&') {
            sb = sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    @Test
    void query() throws Exception {
        String appId = "5ee7b00c";
        String apiSecret = "MmExNmY2YzU5NjY3NGE3YzY5YWUyMWQ2";
        String apiKey = "016e878e8f5fe0870679048f240632b3";
//        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        Date date = new Date();
        String formattedDate = sdf.format(date);

        String tmp = "";
        tmp = "host: " + "spark-api.xf-yun.com" + "\n";
        tmp += "date: " + formattedDate + "\n";
        tmp += "GET " + "/v1.1/chat" + " HTTP/1.1";

        System.out.println(tmp);

        SecretKeySpec secretKeySpec = new SecretKeySpec(apiSecret.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        byte[] rawHMAC = mac.doFinal("tmp".getBytes("UTF-8"));
        String signature = Base64.encodeBase64String(rawHMAC);
        System.out.println(signature);

        String authorizationOrigin = String.format("api_key='%s', algorithm='hmac-sha256', headers='host date request-line', signature='%s'", apiKey, signature);
        String authorization = Base64.encodeBase64String(authorizationOrigin.getBytes(StandardCharsets.UTF_8));

        Map<String, String> v = new HashMap<>();
        v.put("authorization", authorization); // 鉴权生成的authorization
        v.put("date", formattedDate);  // 步骤1生成的date
        v.put("host", "spark-api.xf-yun.com"); // 请求的主机名，根据具体接口替换
        String url = "wss://" + v.get("host") + "/v1.1/chat?" + encodeURL(v);

        System.out.println(url);
    }
}
