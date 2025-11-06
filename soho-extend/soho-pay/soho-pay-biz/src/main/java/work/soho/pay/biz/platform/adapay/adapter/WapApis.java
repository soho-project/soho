package work.soho.pay.biz.platform.adapay.adapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.IpUtils;
import work.soho.pay.api.dto.CreateWalletDto;
import work.soho.pay.api.vo.HftPayCreateWallet;
import work.soho.pay.biz.platform.PayConfig;
import work.soho.pay.biz.platform.model.Order;
import work.soho.pay.biz.platform.payapis.HftCreateWallet;
import work.soho.pay.biz.platform.payapis.Pay;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WapApis extends AbstractAdapayApis implements Pay,HftCreateWallet {

    private String privateKey = "";
    private String publicKey = "";
    private String hftPublicKey = "";
    private String merCustId = "";

    public WapApis(PayConfig payConfig) {
        super(payConfig);
        System.out.println(payConfig);
        this.privateKey = payConfig.getPrivateKey();
        this.hftPublicKey = payConfig.getPayCertificate();
        this.merCustId = payConfig.getMerchantId();

        AdaHttpUtils.hftPublicKey = this.hftPublicKey;
        AdaHttpUtils.publicKey = this.publicKey;
        AdaHttpUtils.privateKey = this.privateKey;
    }

    private String getFullUrl(String path) {
        // TODO 测试接口判断
        if("".equals(merCustId)) {
            // 测试接口
            return "https://hfpay.testpnr.com" + path;
        }
        return "https://hfpay.cloudpnr.com" + path;
    }

    @Override
    public Map<String, String> pay(Order order) throws Exception {
        System.out.println(payConfig);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateString = LocalDate.now().format(formatter);
//        String returnUrl = RequestUtil.getUrl();
//        String url = "https://hfpay.testpnr.com/api/hfpwallet/pay033";
        String url = getFullUrl("/api/hfpwallet/pay033");
        // 1、组装请求参数 data
        Map<String, String> params = new HashMap<>(16);
        params.put(CommonConstants.VERSION, "10");
        params.put(CommonConstants.MER_CUST_ID, merCustId);
        params.put(CommonConstants.ORDER_ID, order.getOutTradeNo());
        params.put(CommonConstants.ORDER_DATE, dateString);
        // 指定客户钱包ID
        if(order.getUserCustId() != null) {
            params.put(CommonConstants.USER_CUST_ID, order.getUserCustId());
        }

        params.put(CommonConstants.DIV_TYPE, "");
//        params.put(CommonConstants.DIV_DETAILS, "[{'divCustId':'6666000105306182','divAcctId':'A01438467','divAmt':'2.00','riskDivType':'1' }]");
        params.put(CommonConstants.DIV_DETAILS, "[{'divCustId':'"+merCustId+"','divAcctId':'"+payConfig.getAppId()+"','divAmt':'"+ order.getAmount().setScale(2, RoundingMode.HALF_UP).toString() +"','riskDivType':'1' }]");
        params.put(CommonConstants.TRANS_AMT, order.getAmount().setScale(2, RoundingMode.HALF_UP).toString());
        params.put(CommonConstants.GOODS_DESC, order.getDescription());
        params.put(CommonConstants.DEV_INFO_JSON, "{'ipAddr':'"+IpUtils.getClientIp()+"','devType':'1','MAC':'D481D7F042F8','IMEI':'355320084666603'}");
        params.put(CommonConstants.OBJECT_INFO, "{'objectType':'','objectName':'1','objectTime':'','marketType':'1','objectAddr':'','chainBelong':'','chainId':'','objectStandard':'','objectTransactions':'','objectIssuerName':'','regMobileNo':'','regDate':'','regIpAddr':'','regCertId':'','regCustId':''}");
        params.put(CommonConstants.MER_PRIV, "mer_priv");
        params.put(CommonConstants.EXTENSION, "test");
//        params.put(CommonConstants.BG_RET_URL, order.getNotifyUrl());
        // TODO  请确认完整地址
        params.put(CommonConstants.BG_RET_URL, "/pay/guest/api/payCallback/adapay/" + payConfig.getId());
//        params.put(CommonConstants.CONTACT_MOBILE, "13911112222");

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
        paramList.add(new BasicNameValuePair("mer_cust_id", merCustId));
        paramList.add(new BasicNameValuePair("data", JSON.toJSONString(params)));
        paramList.add(new BasicNameValuePair("sign", sign));
        System.out.println("#pay033 请求汇付参数为" + JSON.toJSONString(paramList));

        JSONObject res = AdaHttpUtils.httpPost(url, paramList);
        HashMap<String, String> resMap = new HashMap<>();
        resMap.put("resp_code", res.getString("resp_code"));
        resMap.put("resp_desc", res.getString("resp_desc"));
        resMap.put("jump", res.getString("pay_url"));
        return resMap;
    }

    public Map<String, Object> createUser() throws Exception {
        return null;
    }

    /**
     * 创建用户钱包
     *
     * @return
     */
    public HftPayCreateWallet createWallet(CreateWalletDto createWalletDto) {
        String url = getFullUrl("/api/hfpwallet/w00003");
        String orderId = IDGeneratorUtils.snowflake().toString();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateString = LocalDate.now().format(formatter);

        // 1、组装请求参数 data
        Map<String, String> params = new HashMap<>();
        params.put(CommonConstants.VERSION, "10");
        params.put(CommonConstants.MER_CUST_ID, this.payConfig.getMerchantId());
        params.put(CommonConstants.ORDER_DATE, dateString);
        params.put(CommonConstants.ORDER_ID, orderId);
//        params.put(CommonConstants.USER_NAME, "黄文杰");
        params.put("market_type", "2"); // 应用市场
        params.put("acct_usage_type", "wallet");
//        params.put("id_card", "360402198611133850");
        params.put("id_card", createWalletDto.getIdCard());
        params.put("user_name", createWalletDto.getUserName());
        params.put("user_id", createWalletDto.getUserId().toString()); // 本系统跟踪用户ID
        // TODO 处理完整回调地址
        params.put("bg_ret_url", "/pay/guest/api/payCallback/adapay-wallet/" + payConfig.getId()); // 支付成功后异步通知地址
        params.put("ret_url", ""); // 支付成功后跳转地址

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
        paramList.add(new BasicNameValuePair("mer_cust_id", merCustId));
        paramList.add(new BasicNameValuePair("data", JSON.toJSONString(params)));
        paramList.add(new BasicNameValuePair("sign", sign));
        System.out.println("#pay033 请求汇付参数为" + JSON.toJSONString(paramList));

        // 已经开户的返回值
        // {"sys_id":"","extension":"","acct_id":"B00059981","resp_desc":"用户已开户，无需再提交开户","resp_code":"A40203","mer_priv":"","user_cust_id":"6666000100062648","redirect_url":"","mer_cust_id":"6666000100062504","biz_product_id":""}
        // 必选字段已经完毕
        JSONObject jsonObject = AdaHttpUtils.httpPost(url, paramList);
//        JSONObject jsonObject = AdaHttpUtils.myHttpPost(url, paramList);
        if(jsonObject != null) {
            HftPayCreateWallet hftPayCreateWallet =  new HftPayCreateWallet();
            String redirectUrl = jsonObject.getString("redirect_url");
            if(redirectUrl != null && !redirectUrl.isEmpty()) {
                hftPayCreateWallet.setJump(redirectUrl);
                return hftPayCreateWallet;
            } else {
                // 有可能已经创建过了
                hftPayCreateWallet.setUserCustId(jsonObject.getString("user_cust_id"));
                return hftPayCreateWallet;
            }
        }
        return null;
    }

    public HashMap<String, String> queryWallet(String userCustId) {
        String url = getFullUrl("/api/alse/qry016");
        String orderId = IDGeneratorUtils.snowflake().toString();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateString = LocalDate.now().format(formatter);

        // 1、组装请求参数 data
        Map<String, String> params = new HashMap<>();
        params.put(CommonConstants.VERSION, "10");
        params.put(CommonConstants.MER_CUST_ID, this.payConfig.getMerchantId());
        params.put("user_cust_id", userCustId);

        // 2、使用汇付公钥对请求参数中的敏感信息进行加密
        SecurityService.getRsa2EncryptKeyList().forEach(x -> {
            if (params.containsKey(x)) {
                params.put(x, RsaUtils.encrypt(params.get(x), hftPublicKey));
            }
        });

        // 3、使用商户私钥进行签名
        String sign = SecurityService.sign(params, privateKey);

        // 4、组装 post请求数据
        List<NameValuePair> paramList = new ArrayList<>();
        paramList.add(new BasicNameValuePair("version", "10"));
        paramList.add(new BasicNameValuePair("sign_type", "rsa2"));
        paramList.add(new BasicNameValuePair("mer_cust_id", merCustId));
        paramList.add(new BasicNameValuePair("data", JSON.toJSONString(params)));
        paramList.add(new BasicNameValuePair("sign", sign));
        System.out.println("#qry016 请求汇付参数为" + JSON.toJSONString(paramList));

        // 已经开户的返回值
        // 必选字段已经完毕
        JSONObject jsonObject = AdaHttpUtils.httpPost(url, paramList);
        if(jsonObject != null) {
            //TODO 逻辑处理
            JSONArray jsonArray = jsonObject.getJSONArray("acct_info_list");
            if(jsonArray != null) {
                for(int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("acct_id", jsonObject1.getString("acct_id"));
                    map.put("acct_name", jsonObject1.getString("acct_name"));
                    map.put("acct_usage_type", jsonObject1.getString("acct_usage_type"));
                    map.put("user_mobile", jsonObject1.getString("user_mobile"));
                    map.put("user_level", jsonObject1.getString("user_level"));
                    map.put("market_type", jsonObject1.getString("market_type"));
                    map.put("acct_vali_date", jsonObject1.getString("acct_vali_date"));
                    map.put("nft_fee_rec_type", jsonObject1.getString("nft_fee_rec_type"));
                    map.put("stat_flag", jsonObject1.getString("stat_flag"));

                    return map;
                }
            }
        }

        return null;
    }

    /**
     * 获取钱包地址
     *
     * @param userCustId
     * @return
     */
    public HashMap<String, String> queryWalletAddress(String userCustId) {
        String url = getFullUrl("/api/hfpwallet/w00004");
        String orderId = IDGeneratorUtils.snowflake().toString();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateString = LocalDate.now().format(formatter);

        // 1、组装请求参数 data
        Map<String, String> params = new HashMap<>();
        params.put(CommonConstants.VERSION, "10");
        params.put(CommonConstants.MER_CUST_ID, this.payConfig.getMerchantId());
        params.put("user_cust_id", userCustId);
        params.put("order_id", orderId);
        params.put("order_date", dateString);

        // 2、使用汇付公钥对请求参数中的敏感信息进行加密
        SecurityService.getRsa2EncryptKeyList().forEach(x -> {
            if (params.containsKey(x)) {
                params.put(x, RsaUtils.encrypt(params.get(x), hftPublicKey));
            }
        });

        // 3、使用商户私钥进行签名
        String sign = SecurityService.sign(params, privateKey);

        // 4、组装 post请求数据
        List<NameValuePair> paramList = new ArrayList<>();
        paramList.add(new BasicNameValuePair("version", "10"));
        paramList.add(new BasicNameValuePair("sign_type", "rsa2"));
        paramList.add(new BasicNameValuePair("mer_cust_id", merCustId));
        paramList.add(new BasicNameValuePair("data", JSON.toJSONString(params)));
        paramList.add(new BasicNameValuePair("sign", sign));
        System.out.println("#qry016 请求汇付参数为" + JSON.toJSONString(paramList));

        // 已经开户的返回值
        // 必选字段已经完毕
        JSONObject jsonObject = AdaHttpUtils.httpPost(url, paramList);
        if(jsonObject != null) {

            HashMap<String, String> map = new HashMap<>();
            map.put("url", jsonObject.getString("redirect_url"));
            return map;

        }

        return null;
    }

    public HashMap<String, String> queryWalletBalance(String userCustId, String acctId) {
        String url = getFullUrl("/api/alse/qry001");
        String orderId = IDGeneratorUtils.snowflake().toString();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateString = LocalDate.now().format(formatter);

        // 1、组装请求参数 data
        Map<String, String> params = new HashMap<>();
        params.put(CommonConstants.VERSION, "10");
        params.put(CommonConstants.MER_CUST_ID, this.payConfig.getMerchantId());
        params.put("user_cust_id", userCustId);
        params.put("acct_id", acctId);
//        params.put("is_query_guarantee", "1");

        // 2、使用汇付公钥对请求参数中的敏感信息进行加密
        SecurityService.getRsa2EncryptKeyList().forEach(x -> {
            if (params.containsKey(x)) {
                params.put(x, RsaUtils.encrypt(params.get(x), hftPublicKey));
            }
        });

        // 3、使用商户私钥进行签名
        String sign = SecurityService.sign(params, privateKey);

        // 4、组装 post请求数据
        List<NameValuePair> paramList = new ArrayList<>();
        paramList.add(new BasicNameValuePair("version", "10"));
        paramList.add(new BasicNameValuePair("sign_type", "rsa2"));
        paramList.add(new BasicNameValuePair("mer_cust_id", merCustId));
        paramList.add(new BasicNameValuePair("data", JSON.toJSONString(params)));
        paramList.add(new BasicNameValuePair("sign", sign));
        System.out.println("#qry016 请求汇付参数为" + JSON.toJSONString(paramList));

        // 已经开户的返回值
        // 必选字段已经完毕
        JSONObject jsonObject = AdaHttpUtils.httpPost(url, paramList);
        if(jsonObject != null) {

            HashMap<String, String> map = new HashMap<>();
            map.put("user_cust_id", jsonObject.getString("user_cust_id"));
            map.put("acct_id", jsonObject.getString("acct_id"));
            map.put("balance", jsonObject.getString("balance"));
            map.put("acct_balance", jsonObject.getString("acct_balance"));
            map.put("freeze_balance", jsonObject.getString("freeze_balance"));
            map.put("acct_stat", jsonObject.getString("acct_stat"));
            map.put("acct_level", jsonObject.getString("acct_level"));
            map.put("acct_used_limit", jsonObject.getString("acct_used_limit"));
            map.put("fund_in_transit", jsonObject.getString("fund_in_transit"));
            return map;

        }

        return null;
    }
}
