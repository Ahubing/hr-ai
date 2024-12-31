package com.open.ai.eros.common.util;

import com.alibaba.ttl.threadpool.TtlExecutors;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ThreadUtils {

    private static int DEFAULT_CORE_THREAD_SIZE = Runtime.getRuntime().availableProcessors() + 1;

    public static ExecutorService getTtlExecutors(String name) {
        return getTtlExecutors(name, DEFAULT_CORE_THREAD_SIZE, DEFAULT_CORE_THREAD_SIZE, new LinkedBlockingQueue<Runnable>(10000));
    }

    public static ExecutorService getTtlExecutors(String name, int corePoolSize, int maxPoolSize, BlockingQueue<Runnable> queue) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize, maxPoolSize,
                60L, TimeUnit.SECONDS,
                queue,
                getDefaultThreadFactory(name), getDefaultRejectedExecutionHandler());
        return TtlExecutors.getTtlExecutorService(threadPoolExecutor);
    }

    public static ExecutorService getExecutors(String name, int corePoolSize, int maxPoolSize, BlockingQueue<Runnable> queue,
                                                  RejectedExecutionHandler rejectedExecutionHandler) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize, maxPoolSize,
                60L, TimeUnit.SECONDS,
                queue,
                getDefaultThreadFactory(name), rejectedExecutionHandler);
        return TtlExecutors.getTtlExecutorService(threadPoolExecutor);
    }

    private static ThreadFactory getDefaultThreadFactory(String name) {
        ThreadFactory threadFactory = new ThreadFactory() {
            private AtomicInteger ids = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("thread-" + name + "-" + ids.getAndIncrement());
                return thread;
            }
        };
        return threadFactory;
    }

    public static RejectedExecutionHandler getDefaultRejectedExecutionHandler() {
        RejectedExecutionHandler handler = new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                log.warn("Task was rejected, runnable:{}`executor:{}`taskCount:{}", r.toString(), executor.toString(), executor.getTaskCount());
            }
        };
        return handler;
    }

    public static RejectedExecutionHandler getCallerRunsPolicyExecutionHandler() {
        return new ThreadPoolExecutor.CallerRunsPolicy() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                super.rejectedExecution(r, executor);
                log.warn("CallerRunsPolicy was rejected, runnable:{}`executor:{}`taskCount:{}", r.toString(), executor.toString(), executor.getTaskCount());
            }
        };
    }
}
