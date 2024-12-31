package com.open.ai.eros.db.privacy.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 * @date 2023-9-25
 * @apiNote
 */

@ConfigurationProperties(prefix = "privacy.crypto")
@Component
public class CryptoProperties {
    /**
     * 秘钥
     */
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
