package work.soho.pay.biz.platform.adapay.adapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
public class AdaHttpUtils {
    public static String privateKey = "";
    public static String publicKey = "";
    public static String hftPublicKey = "";

    public static String merId = "";

    /**
     * 发起汇付通请求
     *
     * @param url
     * @param paramList
     * @return
     */
    public static JSONObject httpPost(String url, List<NameValuePair> paramList) {
        // 5、发送请求给汇付
        try {
            // 创建 HttpPost
//            HttpPost httpPost = new HttpPost("https://hfpayts.cloudpnr.com/api/hfpwallet/pay033");
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"));
            // 设置请求内容
            httpPost.setEntity(new UrlEncodedFormEntity(paramList, "utf-8"));
            // 创建 HttpClient客户端
            CloseableHttpClient httpClient = HttpClients.createDefault();
            // 执行请求
            CloseableHttpResponse response = httpClient.execute(httpPost);
            // 响应结果
            int statusCode = response.getStatusLine().getStatusCode();
            String result;
            if (statusCode == 200) {
                // 通过 EntityUtils获取返回内容
                Header encode = response.getFirstHeader("Content-Type");
                response.setHeader(encode.getName(), encode.getValue());
                result = EntityUtils.toString(response.getEntity());
                System.out.println("#pay033 汇付返回参数为" + result);
                // 释放 HttpEntity所持有的资源
                EntityUtils.consume(response.getEntity());

                // 6、进行验签，使用汇付公钥进行验签
                JSONObject respJson = JSON.parseObject(result);
                String respSign = respJson.getString(CommonConstants.PARAM_SIGN);
                String respData = respJson.getString(CommonConstants.PARAM_DATA);
                System.out.println("#pay033 验签publicKey为：" + hftPublicKey);
                boolean verifyResult = SecurityService.verify(respData, hftPublicKey, respSign);
                System.out.println("#pay033 验签结果为：" + verifyResult);

                // 验签成功
                if (verifyResult) {
                    // 7、将返回报文中的敏感信息解密（汇付返回报文中，会对敏感信息用商户公钥进行加密；因此商户收到响应报文后需要用商户私钥进行解密）
                    JSONObject respJsonData = JSONObject.parseObject(respData);
                    if (respJsonData != null) {
                        SecurityService.getRsa2EncryptKeyList().forEach(x -> {
                            if (respJsonData.containsKey(x)) {
                                respJsonData.put(x, RsaUtils.decrypt(respJsonData.getString(x), privateKey));
                            }
                        });
                        System.out.println("#pay033 返回报文中的data，解密后信息为：" + respJsonData.toJSONString());
                        // 8、获取跳转收银台地址 pay_url
//                        payUrl = respJsonData.getString("pay_url");
//                        System.out.println("#pay033 跳转收银台地址为 " + payUrl);
                        return respJsonData;
                    }
                } else {
                    // 验签失败
                    System.out.println("验签失败");
                }
            } else {
                System.out.println("#pay033 POST请求失败");
            }
        } catch (Exception e) {
            System.out.println("#pay033 error message" + e.getMessage());
        }
        return null;
    }

    public static JSONObject myHttpPost(String url, Map<String,  String> params) {
        // 2、使用汇付公钥对请求参数中的敏感信息进行加密
        SecurityService.getRsa2EncryptKeyList().forEach(x -> {
            if (params.containsKey(x)) {
//                params.put(x, RsaUtils.encrypt(params.get(x), RsaKeyConstants.HF_PUBLIC_KEY));
                params.put(x, RsaUtils.encrypt(params.get(x), hftPublicKey));
            }
        });

        // 3、使用商户私钥进行签名
        String sign = SecurityService.sign(params, privateKey);

        // 4、组装 post请求数据
        List<NameValuePair> paramList = new ArrayList<>();
        paramList.add(new BasicNameValuePair("version", "10"));
        paramList.add(new BasicNameValuePair("sign_type", "rsa2"));
        paramList.add(new BasicNameValuePair("mer_cust_id", merId));
        paramList.add(new BasicNameValuePair("data", JSON.toJSONString(params)));
        paramList.add(new BasicNameValuePair("sign", sign));
        System.out.println("#pay033 请求汇付参数为" + JSON.toJSONString(paramList));

        return httpPost(url, paramList);
    }
}
