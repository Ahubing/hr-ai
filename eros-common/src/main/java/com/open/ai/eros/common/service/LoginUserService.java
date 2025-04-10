package com.open.ai.eros.common.service;

import com.open.ai.eros.common.vo.CacheUserInfoVo;

public interface LoginUserService {


    /**
     * 根据用户账号获取登录信息
     * @param account
     * @return
     */
    CacheUserInfoVo getCacheUserInfoVo(String account);


}
