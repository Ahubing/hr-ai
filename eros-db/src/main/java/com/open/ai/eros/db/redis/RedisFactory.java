package com.open.ai.eros.db.redis;

import com.open.ai.eros.db.constants.RedisDataSourceConstant;
import com.open.ai.eros.db.util.RedisPoolUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class RedisFactory implements ApplicationContextAware {

    private static Map<String, RedisPoolUtils> redisPoolUtilsMap = new HashMap<>();


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RedisProperties bean = applicationContext.getBean(RedisProperties.class);
        redisPoolUtilsMap.put(RedisDataSourceConstant.EROS_AI_COMMON, new RedisPoolUtils(bean));
    }

    public static RedisPoolUtils getRedisPoolUtils(){
        return redisPoolUtilsMap.get(RedisDataSourceConstant.EROS_AI_COMMON);
    }



    public static RedisPoolUtils getRedisPoolUtilsByAppId(String appId){
        RedisPoolUtils redisPoolUtils;
        if(appId==null || ( redisPoolUtils = redisPoolUtilsMap.get(appId)) == null){
            return getRedisPoolUtils();
        }
        return redisPoolUtils;
    }

}
