package com.open.ai.eros.common.util;

import com.open.ai.eros.common.service.RedisClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 分布式锁工具类
 **/
@Slf4j
@Configuration
public class DistributedLockUtils implements ApplicationContextAware {

    @Autowired
    private RedisClient jedisClient;


    private static final AtomicReference<RedisClient> REDIS_CLIENT_ATOMIC_REFERENCE = new AtomicReference<>(null);

    public static Lock getLock(String name, long leaseTime, TimeUnit timeUnit) {
        return new RedisReentrantLock(getRedisClient(), name, timeUnit.toMillis(leaseTime));
    }

    public static Lock getLock(String name, long leaseTime) {
        return new RedisReentrantLock(getRedisClient(), name, leaseTime);
    }

    @Override
    public void setApplicationContext(ApplicationContext cxt) throws BeansException {
        DistributedLockUtils.REDIS_CLIENT_ATOMIC_REFERENCE.compareAndSet(null, jedisClient);
    }

    private static RedisClient getRedisClient() {
        return Objects.requireNonNull(REDIS_CLIENT_ATOMIC_REFERENCE.get());
    }


    /**
     * 基于 redis 实现的可重入分布式锁。
     * 请注意：一个线程必须在同一个锁对象上获取执行权才有重入效果
     */
    @AllArgsConstructor
    private static class RedisReentrantLock implements Lock {
        private static final long DEFAULT_LEASE_TIME = 30 * 1000;
        private final RedisClient redisClient;
        /**
         * 要锁住的资源名称
         */
        private final String name;
        /**
         * 锁过期时间，毫秒值
         */
        private final long leaseTime;


        @Override
        public boolean tryLock() {
            try {
                Object result = redisClient.eval(
                        "if (redis.call('exists', KEYS[1]) == 0) then " +
                                "redis.call('hincrby', KEYS[1], ARGV[2], 1); " +
                                "redis.call('pexpire', KEYS[1], ARGV[1]); " +
                                "return nil; " +
                                "end; " +
                                "if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then " +
                                "redis.call('hincrby', KEYS[1], ARGV[2], 1); " +
                                "redis.call('pexpire', KEYS[1], ARGV[1]); " +
                                "return nil; " +
                                "end; " +
                                "return redis.call('pttl', KEYS[1]);",
                        1, name,getLessTime(), getLockName(Thread.currentThread().getId()));
                if (Objects.nonNull(result)) {
//                    log.info("获取不到锁 redis lock [{}] fail. ttl:{}", name, result);
                    return false;
                }
            } catch (Exception e) {
                throw new RuntimeException("获取分布式锁时异常：", e);
            }
            return true;
        }


        @Override
        public void unlock() {
            if (isHeldByCurrentThread()) {
                doUnlock();
            }
        }

        private void doUnlock() {
            try {
                Object result = redisClient.eval(
                        "if (redis.call('hexists', KEYS[1], ARGV[2]) == 0) then " +
                                "return nil;" +
                                "end; " +
                                "local counter = redis.call('hincrby', KEYS[1], ARGV[2], -1); " +
                                "if (counter > 0) then " +
                                "redis.call('pexpire', KEYS[1], ARGV[1]); " +
                                "return 0; " +
                                "else " +
                                "redis.call('del', KEYS[1]); " +
                                "return 1; " +
                                "end; " +
                                "return nil;",
                        1, name,getLessTime(), getLockName(Thread.currentThread().getId()));
                if (Objects.isNull(result)) {
//                    log.info("release redis lock [{}] fail. It does not exist or was not acquired by the thread.", name);
                } else if (Objects.equals(result, 0L)) {
//                    log.info("release redis lock [{}] finish.", name);
                } else if (Objects.equals(result, 1L)) {
//                    log.info("release redis lock [{}] finish. It has since been removed.", name);
                }
            } catch (Exception e) {
                throw new RuntimeException("释放分布式锁时异常：", e);
            }
        }

        public boolean isHeldByCurrentThread() {
            return redisClient.hexist(name,getLockName(Thread.currentThread().getId()));
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            throw new UnsupportedOperationException("锁不支持阻塞式的获取方式");
        }


        @Override
        public void lock() {
            throw new UnsupportedOperationException("锁不支持阻塞式的获取方式");
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            throw new UnsupportedOperationException("锁不支持阻塞式的获取方式");
        }

        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException("锁不支持设置条件");
        }

        private String getLockName(long threadId) {
            return  threadId + ":" + hashCode();
        }

        private String getLessTime() {
            return Long.toString(leaseTime <= 0 ? DEFAULT_LEASE_TIME : leaseTime);
        }

        @Override
        public String toString() {
            return "RedisReentrantLock{" +
                    "name='" + name + '\'' +
                    "lockName='" + getLockName(Thread.currentThread().getId()) + '\'' +
                    ", leaseTime=" + leaseTime +
                    '}';
        }
    }

}
