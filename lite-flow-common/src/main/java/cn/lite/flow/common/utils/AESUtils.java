package cn.lite.flow.common.utils;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.Security;

/**
 * Created by ly on 2017/2/15.
 */
public class AESUtils {

    private static final Logger logger = LoggerFactory.getLogger(AESUtils.class);

    private static AESUtils instance = new AESUtils();

    /**
     * sha1加密需要
     */
    private static final char hexDigits[] = {'0','1','2','3','4','5','6','7','8','9',
            'a','b','c','d','e','f'};

    private AESUtils() {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static AESUtils getInstance() {
        return instance;
    }


    /**
     *ECB 分组密码模式 PKCS7Padding填充模式 不需要加密混淆向量
     * @param content
     * @param key
     * @return
     */
    public String encrypt(String content, String key) {
        try {
            byte[] bytes = Base64.decodeBase64(key.getBytes(Constants.DEFAULT_CHARSET));
            SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");   //模式
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encrypted = cipher.doFinal(content.getBytes(Constants.DEFAULT_CHARSET));
            return new String(Base64.encodeBase64(encrypted));       //使用base64做转码功能，相当于二次加密
        } catch (Exception e) {
            logger.error("加密过程中出错!content:{},key:{}", content, key, e);
        }
        return "";
    }

    /**
     *sha1加密
     * @param content
     * @return
     */
    public String encryptWithSha1(String content) {
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(content.getBytes(Constants.DEFAULT_CHARSET));
            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j*2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            logger.error("encryptWithSha1 error!content:{}", content, e);
        }
        return "";
    }

    /**
     * CBC分组密码模式  PKCS7Padding填充模式 使用加密混淆向量iv
     * @param content 加密内容
     * @param key 加密密钥  key数组的长度为16
     * @param iv 加密混淆向量iv  iv数组的长度为16
     * @return
     */
    public String encryptOfDiyIV(String content, String key, String iv) {
        try {
            byte[] keyByte = Base64.decodeBase64(key.getBytes(Constants.DEFAULT_CHARSET));
            byte[] ivByte = Base64.decodeBase64(iv.getBytes(Constants.DEFAULT_CHARSET));
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyByte, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(ivByte));
            byte[] encrypted = cipher.doFinal(content.getBytes(Constants.DEFAULT_CHARSET));
            return Base64.encodeBase64String(encrypted);
        } catch (Exception e) {
            logger.error("加密过程出错!contet:{},key:{}", content, key, e);
        }
        return "";
    }

    /**
     * ECB 分组密码模式 PKCS7Padding填充模式 不需要加密混淆向量
     * @param content
     * @param key
     * @return
     */
    public String decrypt(String content, String key) {
        try {
            byte[] bytes = Base64.decodeBase64(key.getBytes(Constants.DEFAULT_CHARSET));
            SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decodeContent = Base64.decodeBase64(content.getBytes(Constants.DEFAULT_CHARSET));
            byte[] decrypted = cipher.doFinal(decodeContent);
            return new String(decrypted, "utf-8");
        } catch (Exception e) {
            logger.error("解密过程中出错!content:{},key:{}", content, key, e);
        }
        return "";
    }

    /**
     * CBC分组密码模式  PKCS7Padding填充模式 使用加密混淆向量iv
     * @param content 加密内容
     * @param key 加密密钥
     * @param iv 加密混淆向量iv
     * @return
     */
    public String decryptWithDiyIV(String content, String key, String iv) {
        byte[] keyByte = Base64.decodeBase64(key);
        byte[] ivByte = Base64.decodeBase64(iv);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyByte, "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(ivByte));
            byte[] contentByte = Base64.decodeBase64(content.getBytes(Constants.DEFAULT_CHARSET));
            byte[] decrypted = cipher.doFinal(contentByte);
            return new String(decrypted);
        } catch (Exception e) {
            logger.error("解密过程中出错!content:{},key:{},iv:{}", content, key, iv, e);
        }
        return "";
    }
}
