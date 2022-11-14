package work.soho.pay.biz.platform.wechat.http;

import com.wechat.pay.java.core.auth.Credential;
import com.wechat.pay.java.core.auth.Validator;
import com.wechat.pay.java.core.http.HttpMethod;
import com.wechat.pay.java.core.http.HttpRequest;
import com.wechat.pay.java.core.http.JsonRequestBody;
import com.wechat.pay.java.core.http.OriginalResponse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class SohoHttpClient extends com.wechat.pay.java.core.http.AbstractHttpClient{
    public SohoHttpClient(Credential credential, Validator validator) {
        super(credential, validator);
    }

    /**
     * 注意： 如果设置了setDoOutput为 true 则GET请求不生效
     *
     * @param httpRequest
     * @return
     */
    @Override
    protected OriginalResponse innerExecute(HttpRequest httpRequest) {
        try {
            HttpURLConnection conn = null;
            conn = (HttpURLConnection) httpRequest.getUrl().openConnection();
            conn.setRequestMethod(httpRequest.getHttpMethod().name());
            //设置header
            httpRequest.getHeaders().getHeaders().forEach(conn::setRequestProperty);
            conn.setDoInput(true);
            conn.setDoOutput(!(httpRequest.getHttpMethod().equals(HttpMethod.GET)));
            conn.setUseCaches(false);

            if(!(httpRequest.getHttpMethod().equals(HttpMethod.GET))) {
                OutputStream out = conn.getOutputStream();
                if(httpRequest.getBody() != null) {
                    out.write(((JsonRequestBody)httpRequest.getBody()).getBody().getBytes(StandardCharsets.UTF_8));
                }
                out.close();
            }

            int responseCode = conn.getResponseCode();
            InputStream inputStream = conn.getInputStream();
            StringBuffer resultBuf = new StringBuffer();
            BufferedReader buf;
            String line;
            buf = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while((line = buf.readLine()) != null) {
                resultBuf.append(line);
            }
            inputStream.close();
            conn.disconnect();
            //组装header
            HashMap<String, String> headers = new HashMap<>();
            conn.getHeaderFields().forEach((k,l)->{
                String val = "";
                for(String item: l) {
                    if (val.equals("")) {
                        val += item;
                    } else {
                        val += ";" + item;
                    }
                }
                headers.put(k, val);
            });

            OriginalResponse originalResponse = new OriginalResponse.Builder().body(resultBuf.toString())
                        .headers(headers)
                    .statusCode(responseCode)
                    .request(httpRequest)
                    .contentType(conn.getContentType())
                    .build();
            return originalResponse;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected String getHttpClientInfo() {
        return "SohoClient/WechatPay";
    }
}
