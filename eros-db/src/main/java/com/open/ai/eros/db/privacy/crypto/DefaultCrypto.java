package com.open.ai.eros.db.privacy.crypto;


import com.open.ai.eros.db.privacy.enums.Algorithm;
import com.open.ai.eros.db.privacy.utils.AESUtil;
import com.open.ai.eros.db.privacy.utils.CryptoUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Administrator
 * @date 2023-9-25
 * @apiNote
 */
@Slf4j
public class DefaultCrypto implements ICrypto {

//    private static final Logger log = LoggerFactory.getLogger(DefaultCrypto.class.getName());

    private final static String KEY = "eros-ai-68b1-466b-8f6d-256ef53e5066";

    /**
     * 加密
     *
     * @param algorithm 加密算法
     * @param value     加密前的值
     * @param key       秘钥
     * @return 加密后的值
     */
    @Override
    public String encrypt(Algorithm algorithm, String value, String key) throws Exception {
        String result;

        if (key == null || key.length() == 0) {
            key = KEY;
        }

        switch (algorithm) {
            case MD5:
                result = CryptoUtil.encryptBASE64(CryptoUtil.encryptMD5(value.getBytes()));
                break;
            case AES:
                result = AESUtil.encryptBase64(key, value);
                break;
            default:
                result = AESUtil.encryptBase64(key, value);
        }
        return result;
    }

    /**
     * 解密
     *
     * @param algorithm 解密算法
     * @param value     解密前的值
     * @param key       秘钥
     * @return 解密后的值
     */
    @Override
    public String decrypt(Algorithm algorithm, String value, String key) {
        String result;
        if (key == null || key.length() == 0) {
            key = KEY;
        }

        try {
            switch (algorithm) {
                case MD5:
                    log.debug("该算法不支持解密");
                    result = "";
                    break;
                case AES:
                    result = AESUtil.decryptBase64(key, value);
                    break;
                default:
                    result = AESUtil.decryptBase64(key, value);
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            log.debug("值：‘" + value + "’不支持解密");
            result = "";
        }

        return result;

    }

}
