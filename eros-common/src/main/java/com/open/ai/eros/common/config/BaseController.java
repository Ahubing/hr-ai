package com.open.ai.eros.common.config;

import com.open.ai.eros.common.util.SessionUser;
import com.open.ai.eros.common.vo.CacheUserInfoVo;

/**
 * @类名：BaseController
 * @项目名：web-eros-ai
 * @description：
 * @创建人：Administrator
 * @创建时间：2024/8/4 15:43
 */
public class BaseController {

    /**
     * 获取账号
     * @return
     */
    public String getAccount(){
        return SessionUser.getAccount();
    }


    /**
     * 获取当前登录的token
     * @return
     */
    public String getToken(){
        return SessionUser.getToken();
    }


    /**
     * 获取用户id
     * @return
     */
    public Long getUserId(){
        if(SessionUser.get()==null){
            return null;
        }
        return SessionUser.get().getId();
    }

    /**
     * 获取权限
     * @return
     */
    public String getRole(){
        return SessionUser.getRole();
    }


    /**
     * 获取缓存中的用户信息
     *
     * @return
     */
    public CacheUserInfoVo getCacheUserInfo(){
        return SessionUser.get();
    }


}
