package com.open.ai.eros.db.privacy.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;


/**
 * @author Administrator
 * @date 2023-9-25
 * @apiNote
 */

public class AESUtil {

    /**
     * 用于加密用户token
     */
    public static String USER_TOKEN_KEY = "ErosAIAccount";

    private static final Logger log = LoggerFactory.getLogger(AESUtil.class.getName());

    private final static String ALGORITHM = "AES";

    /**
     * @param key     密钥
     * @param content 需要加密的字符串
     * @return 密文字节数组
     */
    private static byte[] encrypt(String key, String content) {
        byte[] rawKey = genKey(key.getBytes());
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(rawKey, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return cipher.doFinal(content.getBytes());
        } catch (Exception e) {
            return null;
        }
    }

    public static String encryptBase64(String key, String content) {
        byte[] encrypt = encrypt(key, content);
        return Base64.getEncoder().encodeToString(encrypt);
    }

    public static String decryptBase64(String key, String content) {
        byte[] decodeContent = Base64.getDecoder().decode(content);
        return decrypt(key, decodeContent);
    }



    /**
     * @param encrypted 密文字节数组
     * @param key       密钥
     * @return 解密后的字符串
     */
    private static String decrypt(String key, byte[] encrypted) {
        byte[] rawKey = genKey(key.getBytes());
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(rawKey, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted);
        } catch (Exception e) {
            log.debug("解密失败以为你返回原值");
            return "";
        }
    }

    /**
     * @param seed 种子数据
     * @return 密钥数据
     */
    private static byte[] genKey(byte[] seed) {
        byte[] rawKey = null;
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(seed);
            // AES加密数据块分组长度必须为128比特，密钥长度可以是128比特、192比特、256比特中的任意一个
            kgen.init(128, secureRandom);
            SecretKey secretKey = kgen.generateKey();
            rawKey = secretKey.getEncoded();
        } catch (NoSuchAlgorithmException ignored) {
        }
        return rawKey;
    }


    public static void main(String[] args) {
        // 密钥的种子，可以是任何形式，本质是字节数组
        // 密码的明文
        String clearPwd = "jiuhao";

        // 密码加密后的密文
        byte[] encryptedByteArr = encrypt(USER_TOKEN_KEY, clearPwd);
        String encryptedPwd = Base64.getEncoder().encodeToString(encryptedByteArr);
        System.out.println(encryptedPwd);

        // 解密后的字符串
        byte[] decode = Base64.getDecoder().decode("9sihQtVbG9X8bOctdMRc50Z4S60AhFqO+Oc9rNnauzU=");
        String decryptedPwd = decrypt(USER_TOKEN_KEY, decode);
        System.out.println(decryptedPwd);
    }

}

