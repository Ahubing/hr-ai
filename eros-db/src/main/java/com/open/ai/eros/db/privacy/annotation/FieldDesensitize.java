package com.open.ai.eros.db.privacy.annotation;


import com.open.ai.eros.db.privacy.desensitizer.DefaultDesensitizer;
import com.open.ai.eros.db.privacy.desensitizer.IDesensitizer;

import java.lang.annotation.*;

/**
 * @author Administrator
 * @date 2023-9-25
 * @apiNote
 */

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface FieldDesensitize {

    /**
     * 填充值
     *
     * @return
     */
    String fillValue() default "*";

    /**
     * 脱敏器
     *
     * @return
     */
    Class<? extends IDesensitizer> desensitizer() default DefaultDesensitizer.class;
}
