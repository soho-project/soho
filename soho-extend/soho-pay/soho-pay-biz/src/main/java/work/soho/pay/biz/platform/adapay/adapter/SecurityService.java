package work.soho.pay.biz.platform.adapay.adapter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author zhikang.yang
 * @description security service
 * @date 2023/4/3
 **/
public class SecurityService {

    private final static Logger logger = LoggerFactory.getLogger(SecurityService.class);

    /**
     * RSA2模式加密字段列表
     */
    private static final List<String> RSA2_ENCRYPT_KEY_LIST = Collections.unmodifiableList(
            Arrays.asList("card_no", "bank_acct_num", "card_num", "id_card_no", "id_card", "legal_id_card", "cert_id", "user_mobile", "legal_mobile", "contact_mobile", "card_mobile"));

    /**
     * 签名
     * 使用私钥进行签名
     *
     * @param map 待加签内容
     * @param privateKey 私钥
     * @return 加签结果
     */
    public static String sign(Map<String, ?> map, String privateKey) {
        // 待签名明文按照 key的 ascii顺序排序
        String jsonDataStr = RsaUtils.sort4JsonString(map);
        logger.info("待加签内容：" + jsonDataStr);
        // 加签
        String signResult = RsaUtils.sign(jsonDataStr, privateKey);

        if (StringUtils.isNotEmpty(signResult)) {
            logger.info("加签结果：{}", signResult);
            return signResult;
        } else {
            return "";
        }
    }

    /**
     * 验签
     * @param jsonData 待验签内容
     * @param publicKey 公钥
     * @param sign 密文
     * @return 验签结果
     */
    public static boolean verify(String jsonData, String publicKey, String sign) {
        // 将 json对象的 key 按 ascii排序序列化成字符串
        String data = RsaUtils.sort4JsonString(jsonData);
        logger.info("待验签内容：" + data);
        return RsaUtils.verify(data, publicKey, sign);
    }

    /**
     * 获取 RSA2模式的敏感字段列表
     *
     * @return RSA2模式的敏感字段列表
     */
    public static List<String> getRsa2EncryptKeyList() {
        return RSA2_ENCRYPT_KEY_LIST;
    }
}
