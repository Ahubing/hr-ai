package com.open.ai.eros.common.service;


import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis操作Service
 * Created by macro on 2020/3/3.
 */
public interface RedisClient {


    Set<Tuple> zrevrangeWithScores(String key, long start, long stop);


    /**
     * 用redis的hash结构,用hset存入
     * @param key
     * @param bean
     * @param <T>
     */
    <T>void setBeanToRedis(String key,T bean);

    /**
     * 从set集合随机删除1个值
     *
     * @param key
     * @return
     */
    String spop(String key);


    /**
     * 从set集合随机删除1个值
     *
     * @param key
     * @return
     */
    Set<String> spop(String key,long count);


    /**
     * 保存属性
     */
    void set(String key, String value, long time);

    /**
     * 保存属性
     */
    void set(String key, String value);

    /**
     * 获取属性
     */
    String get(String key);

    /**
     * 删除属性
     */
    Long del(String key);

    /**
     * zset 删除
     * @param key
     * @param members
     * @return
     */
     Long zrem(String key, String... members);

    /**
     * 设置过期时间
     */
    Long expire(String key, long time);

    /**
     * 获取过期时间
     */
    Long ttl(String key);

    /**
     * 判断是否有该属性
     */
    Boolean exists(String key);

    /**
     * 按delta递增
     */
    Long incr(String key, long delta);

    /**
     * 按delta递减
     */
    Long decr(String key, long delta);

    /**
     * 获取Hash结构中的属性
     */
    String hget(String key, String hashKey);


    /**
     * 向Hash结构中放入一个属性
     */
    void hset(String key, String hashKey, String value);

    /**
     * 直接获取整个Hash结构
     */
    Map<String, String> hgetAll(String key);


    /**
     * 直接设置整个Hash结构
     */
    Long hset(String key, Map<String, String> map);

    /**
     * 删除Hash结构中的属性
     */
    Long hdel(String key, String... hashKey);

    /**
     * 判断Hash结构中是否有该属性
     */
    Boolean hexist(String key, String hashKey);

    /**
     * Hash结构中属性递增
     */
    Long hincr(String key, String hashKey, Long delta);

    /**
     * 获取Set结构
     */
    Set<String> smembers(String key);

    /**
     * 向Set结构中添加属性
     */
    Long sadd(String key, String... values);

    /**
     * 向Set结构中添加属性
     */
    Long sadd(String key, long time, String... values);

    /**
     * 是否为Set中的属性
     */
    Boolean sismember(String key, String value);

    /**
     * 获取Set结构的长度
     */
    Long scard(String key);

    /**
     * 删除Set结构中的属性
     */
    Long srem(String key, String... values);

    /**
     * 获取List结构中的属性
     */
    List<String> lrange(String key, long start, long end);

    /**
     * 获取List结构的长度
     */
    Long lsize(String key);

    /**
     * 根据索引获取List中的属性
     */
    String lindex(String key, long index);

    /**
     * 向List结构中添加属性
     */
    Long brpoplpush(String key, String value);

    /**
     * 向List结构中添加属性
     */
    String brpoplpush(String key, String value, long time);

    /**
     * 向List结构中批量添加属性
     */
    Long lpush(String key, String... values);


    /**
     * 从List结构中移除属性
     */
    Long lrem(String key, long count, String value);


    /**
     * 向List结构中批量添加属性
     */
    Long rpush(String key, String... values);



    /**
     * 监视key，开始事务
     */
    String watch(String... var1);

    /**
     * 开启事务
     */
    Transaction multi();

    Object eval(final String script, final int keyCount, final String... params);

}
