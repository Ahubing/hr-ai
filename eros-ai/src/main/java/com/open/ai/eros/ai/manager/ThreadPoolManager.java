package com.open.ai.eros.ai.manager;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class ThreadPoolManager {


    private static ThreadFactory docsSliceThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("email-pool-%d").build();
    public static ThreadPoolExecutor docsSlicePool = new ThreadPoolExecutor(2, 10, 10, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(10000), docsSliceThreadFactory, getCallerRunsPolicyExecutionHandler());


    private static ThreadFactory maskThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("mask-pool-%d").build();

    public static ThreadPoolExecutor maskPool = new ThreadPoolExecutor(1, 20, 10, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(10000), maskThreadFactory, getCallerRunsPolicyExecutionHandler());


    public static RejectedExecutionHandler getCallerRunsPolicyExecutionHandler() {
        return new ThreadPoolExecutor.CallerRunsPolicy() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                super.rejectedExecution(r, executor);
                log.warn("CallerRunsPolicy was rejected, runnable:{}`executor:{}`taskCount:{}", r.toString(), executor.toString(), executor.getTaskCount());
            }
        };
    }


    private static ThreadFactory modelConfigThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("model-config-pool-%d").build();
    public static ThreadPoolExecutor modelConfigPool = new ThreadPoolExecutor(1, 20, 10, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(10000), modelConfigThreadFactory, getCallerRunsPolicyExecutionHandler());


    private static ThreadFactory sseChatFactory = new ThreadFactoryBuilder()
            .setNameFormat("model-config-pool-%d").build();
    public static ThreadPoolExecutor sseChatPool = new ThreadPoolExecutor(10, 2000, 100, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(10000), sseChatFactory, getCallerRunsPolicyExecutionHandler());


}
