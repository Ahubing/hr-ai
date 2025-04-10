package com.open.ai.eros.db.privacy.enums;

/**
 *@author Administrator
 * @date 2023-9-25
 * @apiNote
 */
public enum Algorithm {

    /**
     * 不可逆加密 MD5
     * 对称加密  AES （推荐；速度快、可解密）
     */
    MD5, AES;


    Algorithm() {
    }
}
