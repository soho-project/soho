package work.soho.pay.biz.platform.adapay.adapter;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author zhikang.yang
 * @description RSA加解签工具类
 * @date 2023/4/3
 **/
public class RsaUtils {

    private static final Logger logger = LoggerFactory.getLogger(RsaUtils.class);

    /**
     * RSA算法
     */
    private static final String RSA_ALGORITHM = "RSA";
    /**
     * RSA2签名算法
     */
    private static final String RSA_SIGNATURE_ALGORITHM = "SHA256WithRSA";
    /**
     * RSA公钥key
     */
    public static final String RSA_PUBLIC_KEY = "RSA_PUBLIC_KEY";
    /**
     * RSA私钥key
     */
    public static final String RSA_PRIVATE_KEY = "RSA_PRIVATE_KEY";

    /**
     * 生成 RSA公私钥对：密钥格式 PKCS8
     *
     * @param keysize 密钥长度：1024, 2048
     * @return RSA公、私钥对Map<String, String>
     */
    public static Map<String, String> generateKeyPair(Integer keysize) {
        // 生成RSA Key
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
            keyPairGenerator.initialize(keysize);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            // Key --> Base64
            String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());

            Map<String, String> ret = new HashMap<String, String>();
            ret.put(RSA_PUBLIC_KEY, publicKeyBase64);
            ret.put(RSA_PRIVATE_KEY, privateKeyBase64);
            return ret;
        } catch (Exception e) {
            logger.error("Exception", e);
            return null;
        }
    }

    /**
     * 使用公钥加密
     *
     * @param content   明文
     * @param publicKey 公钥
     * @return 密文
     */
    public static String encrypt(String content, String publicKey) {
        if (null == content) {
            return null;
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(publicKey);
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance(RSA_ALGORITHM).generatePublic(new X509EncodedKeySpec(decoded));
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            String outStr = Base64.getEncoder().encodeToString(cipher.doFinal(content.getBytes(StandardCharsets.UTF_8)));
            return outStr;
        } catch (Exception ex) {
            logger.warn("加密失败", ex);
            return null;
        }
    }

    /**
     * 使用私钥解密
     *
     * @param encryptContent 密文
     * @param privateKey     私钥
     * @return 明文
     */
    public static String decrypt(String encryptContent, String privateKey) {
        if (null == encryptContent) {
            return null;
        }
        try {
            byte[] inputByte = Base64.getDecoder().decode(encryptContent.getBytes(StandardCharsets.UTF_8));
            byte[] decoded = Base64.getDecoder().decode(privateKey);
            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance(RSA_ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(decoded));
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            String outStr = new String(cipher.doFinal(inputByte));
            return outStr;
        } catch (Exception ex) {
            logger.warn("解密失败", ex);
            return null;
        }
    }

    /**
     * RSA私钥签名：签名方式 SHA256WithRSA
     *
     * @param data             待签名字符串
     * @param privateKeyBase64 私钥（Base64编码）
     * @return 签名byte[]
     */
    public static String sign(String data, String privateKeyBase64) {
        // Base64 --> Key
        try {
            byte[] bytes = Base64.getDecoder().decode(privateKeyBase64);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
            KeyFactory keyFactory;
            keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            // Sign
            Signature signature = Signature.getInstance(RSA_SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature.sign()).replaceAll(" ", "");
        } catch (Exception e) {
            logger.warn("签名失败", e);
            return null;
        }
    }

    /**
     * RSA公钥验签
     *
     * @param data            待签名字符串
     * @param publicKeyBase64 公钥（Base64编码）
     * @return 验签结果
     */
    public static boolean verify(String data, String publicKeyBase64, String sign) {
        // Base64 --> Key
        try {
            byte[] bytes = Base64.getDecoder().decode(publicKeyBase64);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
            KeyFactory keyFactory;
            keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            // verify
            Signature signature = Signature.getInstance(RSA_SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.getDecoder().decode(sign));
        } catch (Exception e) {
            logger.warn("验签失败", e);
            return false;
        }

    }

    /**
     * 将 JSON明文按照 key的 ascii顺序排序后形成待签名明文
     *
     * @param sourceJson 源 JSON串
     * @return 待签名明文
     */
    public static String sort4JsonString(String sourceJson) {
        if (StringUtils.isBlank(sourceJson)) {
            return StringUtils.EMPTY;
        }
        try {
            TreeMap jsonObject = JSON.parseObject(sourceJson, TreeMap.class);
            return JSON.toJSONString(jsonObject);
        } catch (Exception ex) {
            logger.warn("待签名明文排序失败", ex);
            return null;
        }
    }

    /**
     * 将明文按照 key的 ascii顺序排序后形成待签名明文
     *
     * @param sourceMap 明文
     * @return 待签名明文
     */
    public static String sort4JsonString(Map sourceMap) {
        if (null == sourceMap) {
            return StringUtils.EMPTY;
        }
        return JSON.toJSONString(new TreeMap<>(sourceMap));
    }
}
