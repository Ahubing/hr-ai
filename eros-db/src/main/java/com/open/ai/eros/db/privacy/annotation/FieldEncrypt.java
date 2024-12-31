package com.open.ai.eros.db.privacy.annotation;


import com.open.ai.eros.db.privacy.crypto.DefaultCrypto;
import com.open.ai.eros.db.privacy.crypto.ICrypto;
import com.open.ai.eros.db.privacy.enums.Algorithm;

import java.lang.annotation.*;

/**
 * @author Administrator
 * @date 2023-9-25
 * @apiNote 字段加密
 */

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface FieldEncrypt {

    /**
     * 秘钥
     *
     * @return
     */
    String key() default "";

    /**
     * 加密解密算法
     *
     * @return
     */
    Algorithm algorithm() default Algorithm.AES;

    /**
     * 加密解密器
     *
     * @return
     */
    Class<? extends ICrypto> iCrypto() default DefaultCrypto.class;

}
