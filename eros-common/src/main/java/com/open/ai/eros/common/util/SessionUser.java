package com.open.ai.eros.common.util;





import com.open.ai.eros.common.vo.CacheUserInfoVo;

import java.util.Optional;

/**
 * 保存用户的线程中的快照信息
 */
public class SessionUser {

    private static ThreadLocal<CacheUserInfoVo> sessionUser = new ThreadLocal<>();

    public static void setSessionUserInfo(CacheUserInfoVo adminUserVO) {
        sessionUser.set(adminUserVO);
    }

    public static CacheUserInfoVo get(){
        return sessionUser.get();
    }

    public static Long getUserId(){
        CacheUserInfoVo cacheUserInfoVo = sessionUser.get();
        if(cacheUserInfoVo == null){
            return null;
        }
        return cacheUserInfoVo.getId();
    }

    /**
     * 用户登录的信息
     * @return
     */
    public static Optional<CacheUserInfoVo> getUserInfoVO(){
        CacheUserInfoVo cacheUserInfoVo = get();
        if(cacheUserInfoVo == null){
            return Optional.empty();
        }
        return Optional.of(cacheUserInfoVo);
    }

    public static Optional<String> getUserName(){
        CacheUserInfoVo cacheUserInfoVo = sessionUser.get();
        if(cacheUserInfoVo == null){
            return Optional.empty();
        }
        return Optional.of(cacheUserInfoVo.getUserName());
    }

    public static String getAccount(){
        CacheUserInfoVo cacheUserInfoVo = sessionUser.get();
        if(cacheUserInfoVo == null){
            return null;
        }
        return cacheUserInfoVo.getEmail();
    }


    public static String getToken(){
        CacheUserInfoVo cacheUserInfoVo = sessionUser.get();
        if(cacheUserInfoVo == null){
            return null;
        }
        return cacheUserInfoVo.getToken();
    }




    /**
     * 角色
     * @return
     */
    public static String getRole(){
        CacheUserInfoVo cacheUserInfoVo = sessionUser.get();
        if(cacheUserInfoVo == null){
            return null;
        }
        return sessionUser.get().getRole();
    }


    public static void remove(){
        sessionUser.remove();
    }


}
