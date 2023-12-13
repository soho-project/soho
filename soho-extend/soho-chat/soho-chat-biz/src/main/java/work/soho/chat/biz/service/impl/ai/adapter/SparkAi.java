package work.soho.chat.biz.service.impl.ai.adapter;

import org.apache.commons.codec.binary.Base64;
import work.soho.chat.api.request.AiRequest;
import work.soho.chat.api.service.ai.AiApiService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class SparkAi implements AiApiService {
    private SparkAiOptions sparkAiOptions;

    SparkAi(SparkAiOptions sparkAiOptions) {
        this.sparkAiOptions = sparkAiOptions;
    }

    @Override
    public String query(String question) {
        return null;
    }

    @Override
    public String chatQuery(LinkedList<AiRequest.Message> list) {
        return null;
    }

    public static String encodeURL(Map<String, String> map) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(URLEncoder.encode(entry.getKey(), "UTF-8")).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            sb.append("&");
        }
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '&') {
            sb = sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 签名客户端URL
     *
     * @return
     * @throws Exception
     */
    public String signatureUrl() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        Date date = new Date();
        String formattedDate = sdf.format(date);

        String tmp = "";
        tmp = "host: " + "spark-api.xf-yun.com" + "\n";
        tmp += "date: " + formattedDate + "\n";
        tmp += "GET " + "/v1.1/chat" + " HTTP/1.1";

        System.out.println(tmp);

        SecretKeySpec secretKeySpec = new SecretKeySpec(sparkAiOptions.getApiSecret().getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        byte[] rawHMAC = mac.doFinal("tmp".getBytes("UTF-8"));
        String signature = Base64.encodeBase64String(rawHMAC);
        System.out.println(signature);

        String authorizationOrigin = String.format("api_key='%s', algorithm='hmac-sha256', headers='host date request-line', signature='%s'", sparkAiOptions.getApiKey(), signature);
        String authorization = Base64.encodeBase64String(authorizationOrigin.getBytes(StandardCharsets.UTF_8));

        Map<String, String> v = new HashMap<>();
        v.put("authorization", authorization); // 鉴权生成的authorization
        v.put("date", formattedDate);  // 步骤1生成的date
        v.put("host", "spark-api.xf-yun.com"); // 请求的主机名，根据具体接口替换
        String url = "wss://" + v.get("host") + "/v1.1/chat?" + encodeURL(v);

        System.out.println(url);
        return url;
    }
}
