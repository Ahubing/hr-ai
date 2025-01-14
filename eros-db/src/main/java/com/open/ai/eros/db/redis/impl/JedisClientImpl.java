package com.open.ai.eros.db.redis.impl;

import com.open.ai.eros.common.service.RedisClient;
import com.open.ai.eros.db.redis.RedisFactory;
import com.open.ai.eros.db.util.RedisPoolUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Primary
@Component("common")
public class JedisClientImpl implements RedisClient {


    @Override
    public Set<Tuple> zrevrangeWithScores(String key, long start, long stop) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.zrevrangeWithScores(key, start, stop);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    @Override
    public void set(String key, String value, long time) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            jedis.setex(key,time,value);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }


    @Override
    public String spop(String key) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.spop(key);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }



    @Override
    public Set<String> spop(String key, long count) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
             return jedis.spop(key, count);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }


    @Override
    public void set(String key, String value) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            jedis.set(key,value);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    @Override
    public String get(String key) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.get(key);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    @Override
    public Long del(String key) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.del(key);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }



    @Override
    public Long expire(String key, long time) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.expire(key,time);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    @Override
    public Long ttl(String key) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.ttl(key);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    @Override
    public Boolean exists(String key) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.exists(key);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    @Override
    public Long incr(String key, long delta) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.incr(key);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    @Override
    public Long decr(String key, long delta) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.decr(key);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    @Override
    public String hget(String key, String hashKey) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.hget(key,hashKey);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }


    @Override
    public void hset(String key, String hashKey, String value) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            jedis.hset(key,hashKey,value);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.hgetAll(key);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }


    @Override
    public Long hset(String key, Map<String, String> map) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.hset(key,map);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    @Override
    public Long hdel(String key, String... hashKey) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.hdel(key,hashKey);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    @Override
    public Boolean hexist(String key, String hashKey) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.hexists(key,hashKey);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    @Override
    public Long hincr(String key, String hashKey, Long delta) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.hincrBy(key,hashKey,delta);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }


    @Override
    public Set<String> smembers(String key) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.smembers(key);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    @Override
    public Long sadd(String key, String... values) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.sadd(key,values);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    @Override
    public Long sadd(String key, long time, String... values) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            jedis.sadd(key);
            return jedis.expire(key,time);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    @Override
    public Boolean sismember(String key, String value) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.sismember(key,value);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    @Override
    public Long scard(String key) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.scard(key);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    @Override
    public Long srem(String key, String... values) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.srem(key,values);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    @Override
    public List<String> lrange(String key, long start, long end) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.lrange(key,start,end);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    @Override
    public Long lsize(String key) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.llen(key);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    @Override
    public String lindex(String key, long index) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.lindex(key,index);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    @Override
    public Long brpoplpush(String key, String value) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.lpush(key,value);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    @Override
    public String brpoplpush(String key, String value, long time) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.brpoplpush(key,value,(int)time);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    @Override
    public Long lpush(String key, String... values) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.lpush(key,values);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }


    @Override
    public Long lrem(String key, long count, String value) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.lrem(key,count,value);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    @Override
    public Long rpush(String key, String... values) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.rpush(key,values);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }


    @Override
    public String watch(String... var1) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.watch(var1);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }



    public Transaction multi(){
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return  jedis.multi();
        }finally {
            redisPoolUtils.close(jedis);
        }
    }



    public Long zadd(String var1, double var2, String var4){
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return  jedis.zadd(var1,var2,var4);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }


    public Double zscore(String var1, String var2){
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return  jedis.zscore(var1,var2);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    public Long zrem(String var1, String... members){
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return  jedis.zrem(var1,members);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    public Set<String> zrange(String var1, long var2, long var4){
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return  jedis.zrange(var1,var2,var4);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    public Set<String> zrangeByScore(String var1, double var2, double var4){
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            return jedis.zrangeByScore(var1,var2,var4);
        }finally {
            redisPoolUtils.close(jedis);
        }
    }

    public Jedis getJedis(){
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        return redisPoolUtils.getJedis();
    }

    public void closeJedis(Jedis jedis){
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        redisPoolUtils.close(jedis);
    }


    @Override
    public Object eval(String script, int keyCount, String... params) {
        RedisPoolUtils redisPoolUtils = RedisFactory.getRedisPoolUtils();
        Jedis jedis = redisPoolUtils.getJedis();
        try {
            Object result = jedis.eval(script, keyCount,  params);
            return  result;
        }finally {
            redisPoolUtils.close(jedis);
        }
    }


    /**
     * 用redis的hash结构,用hset存入
     * @param key
     * @param bean
     * @param <T>
     */
    public <T>void setBeanToRedis(String key,T bean){
        String luaScript = "if redis.call('EXISTS', KEYS[1]) == 0 then redis.call('HMSET', KEYS[1], unpack(ARGV)) end";
        String[] beanArray = parsingBeanToArray(key,bean);
        eval(luaScript,1,beanArray);
    }


    public <T> String[] parsingBeanToArray(String luaKey,T bean) {
        Map<String, String> beanMap = Arrays.stream(bean.getClass().getDeclaredFields()) // 获取所有字段
                .collect(Collectors.toMap(Field::getName, field -> {  // 创建一个Map，键是字段名，值是字段值
                    try {
                        field.setAccessible(true);  // 使字段可以访问
                        Object value = field.get(bean);  // 获取字段值
                        return value != null ? value.toString() : "";  // 将字段值转换为字符串
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }))
                .entrySet().stream() // 将Map转换为Stream<Entry<String, String>>
                .filter(entry -> entry.getValue() != null) // 仅保留值非空的Entry
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)); // 将过滤后的Entry收集回一个新的Map

        List<String> result = new ArrayList<>();
        result.add(luaKey);
        beanMap.forEach((key, value) -> {
            if (!StringUtils.isAnyBlank(key,value)){
                result.add(key);
                result.add(value);
            }
        });

        return result.toArray(new String[0]);
    }

    public <T> Map<String, String> parsingBean(T bean) {
        Map<String, String> beanMap = Arrays.stream(bean.getClass().getDeclaredFields()) // 获取所有字段
                .collect(Collectors.toMap(Field::getName, field -> {  // 创建一个Map，键是字段名，值是字段值
                    try {
                        field.setAccessible(true);  // 使字段可以访问
                        Object value = field.get(bean);  // 获取字段值
                        return value != null ? value.toString() : "";  // 将字段值转换为字符串
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }))
                .entrySet().stream() // 将Map转换为Stream<Entry<String, String>>
                .filter(entry -> entry.getValue() != null) // 仅保留值非空的Entry
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)); // 将过滤后的Entry收集回一个新的Map
        return beanMap;
    }


}
