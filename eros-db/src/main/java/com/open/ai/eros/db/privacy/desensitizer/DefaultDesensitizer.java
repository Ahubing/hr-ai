package com.open.ai.eros.db.privacy.desensitizer;

import com.open.ai.eros.db.privacy.crypto.DefaultCrypto;
import com.open.ai.eros.db.privacy.utils.DesensitizeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 * @date 2023-9-25
 * @apiNote
 */
public class DefaultDesensitizer implements IDesensitizer {

    private static final Logger log = LoggerFactory.getLogger(DefaultCrypto.class.getName());

    /**
     * 执行脱敏处理
     *
     * @param value     要脱敏的值
     * @param fillValue 填充的副号
     * @return
     */
    @Override
    public String execute(String value, String fillValue) {
        if (value == null || value.length() == 0 || fillValue == null || fillValue.length() == 0) {
            return "";
        }
        String sensitiveInfo = DesensitizeUtil.encryptSensitiveInfo(value, fillValue);
        log.debug("脱敏原值：" + value);
        log.debug("脱敏后值：" + sensitiveInfo);
        return sensitiveInfo;
    }
}
