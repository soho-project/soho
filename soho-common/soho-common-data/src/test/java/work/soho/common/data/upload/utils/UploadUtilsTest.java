package work.soho.common.data.upload.utils;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import work.soho.test.TestApp;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@ContextConfiguration
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("local")
@Log4j2
class UploadUtilsTest {
    @Test
    public void testUpload() {
        String filePath = UploadUtils.upload("soho", "c/d/e/fabctest.txt", "test by fang");
        System.out.println("test by fang...");
        System.out.println(filePath);
    }

    @Test
    public void testS3Upload() {
        String filePath = UploadUtils.upload("s3", "c/d/e/aaaa.txt", "test by fang");
        System.out.println("test by fang...");
        System.out.println(filePath);
    }

    @Test
    public void testS3Upload1() throws Exception {
        S3Client s3 = S3Client.builder()
                .endpointOverride(URI.create("http://localhost:9000"))
                .region(Region.of("us-east-1"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")
                ))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)       // 使用 path-style
                        .checksumValidationEnabled(false)  // 关闭 payload 校验
                        .chunkedEncodingEnabled(false)     // 关闭 chunked / streaming
                        .build())
                .overrideConfiguration(b -> b
                        .putHeader("content-type", "application/octet-stream") // 显式设置 header
                )
                .build();

        PutObjectRequest req = PutObjectRequest.builder()
                .bucket("test-bucket")
                .key("ttaertat.txt")
                .build();

        s3.putObject(req, RequestBody.fromString("Hello, S3 Storage!"));
    }

    @Test
    public void testS3Upload2() throws Exception {
        String bucket = "test-bucket";
        String key = "hello2.txt";
        String content = "Hello, S3 Storage!";

        // 1. 创建 Bucket
        System.out.println("=== 1. 创建 Bucket ===");
        sendRequest("PUT", "/" + bucket, "", null);

        // 2. 上传对象
        System.out.println("=== 2. 上传对象 ===");
        sendRequest("PUT", "/" + bucket + "/" + key, "", content.getBytes(StandardCharsets.UTF_8));

        // 3. 下载对象
        System.out.println("=== 3. 下载对象 ===");
        sendRequest("GET", "/" + bucket + "/" + key, "", null);

        // 4. 列出对象
        System.out.println("=== 4. 列出对象 ===");
        sendRequest("GET", "/" + bucket, "", null);
    }

    private static final String ACCESS_KEY = "AKIDEXAMPLE";
    private static final String SECRET_KEY = "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY";
    private static final String REGION = "us-east-1";
    private static final String SERVICE = "s3";
    private static final String ENDPOINT = "http://localhost:9000";

    private static void sendRequest(String method, String path, String query, byte[] body) throws Exception {
        String amzDate = getAmzDate();
        String dateStamp = amzDate.substring(0, 8);

        String payloadHash = sha256Hex(body != null ? body : new byte[0]);

        String canonicalUri = encodeUri(path);

        // canonical headers
        String canonicalHeaders = "host:localhost:9000\n" +
                "x-amz-content-sha256:" + payloadHash + "\n" +
                "x-amz-date:" + amzDate + "\n";
        String signedHeaders = "host;x-amz-content-sha256;x-amz-date";

        String canonicalRequest = method + "\n" +
                canonicalUri + "\n" +
                query + "\n" +
                canonicalHeaders + "\n" +
                signedHeaders + "\n" +
                payloadHash;

        String canonicalRequestHash = sha256Hex(canonicalRequest.getBytes(StandardCharsets.UTF_8));
        String credentialScope = dateStamp + "/" + REGION + "/" + SERVICE + "/aws4_request";

        String stringToSign = "AWS4-HMAC-SHA256\n" +
                amzDate + "\n" +
                credentialScope + "\n" +
                canonicalRequestHash;

        byte[] signingKey = getSignatureKey(SECRET_KEY, dateStamp, REGION, SERVICE);
        String signature = hmacSHA256Hex(signingKey, stringToSign);

        String authorizationHeader = "AWS4-HMAC-SHA256 Credential=" + ACCESS_KEY + "/" + credentialScope +
                ", SignedHeaders=" + signedHeaders + ", Signature=" + signature;

        // 构造 URL
        URL url = new URL(ENDPOINT + path + (query.isEmpty() ? "" : "?" + query));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", authorizationHeader);
        conn.setRequestProperty("x-amz-date", amzDate);
        conn.setRequestProperty("x-amz-content-sha256", payloadHash);

        if (body != null && body.length > 0) {
            conn.setRequestProperty("Content-Length", String.valueOf(body.length));
            OutputStream os = conn.getOutputStream();
            os.write(body);
            os.flush();
            os.close();
        }

        int status = conn.getResponseCode();
        InputStream is = status < 400 ? conn.getInputStream() : conn.getErrorStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        br.close();
        conn.disconnect();
    }

    private static String getAmzDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    private static String sha256Hex(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(data);
        return bytesToHex(digest);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }

    private static byte[] hmacSHA256(byte[] key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    private static String hmacSHA256Hex(byte[] key, String data) throws Exception {
        return bytesToHex(hmacSHA256(key, data));
    }

    private static byte[] getSignatureKey(String secretKey, String dateStamp, String regionName, String serviceName) throws Exception {
        byte[] kSecret = ("AWS4" + secretKey).getBytes(StandardCharsets.UTF_8);
        byte[] kDate = hmacSHA256(kSecret, dateStamp);
        byte[] kRegion = hmacSHA256(kDate, regionName);
        byte[] kService = hmacSHA256(kRegion, serviceName);
        return hmacSHA256(kService, "aws4_request");
    }

    private static String encodeUri(String path) throws UnsupportedEncodingException {
        String[] parts = path.split("/");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) sb.append("/").append(java.net.URLEncoder.encode(part, "UTF-8").replace("+", "%20")
                    .replace("*", "%2A").replace("%7E", "~"));
        }
        if (path.endsWith("/")) sb.append("/");
        if (path.isEmpty()) sb.append("/");
        return sb.toString();
    }
}
