package com.open.ai.eros.user.manager;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class ThreadPoolManager {


    private static ThreadFactory userBalanceFactory = new ThreadFactoryBuilder()
            .setNameFormat("user-balance-%d").build();
    public static ThreadPoolExecutor userBalancePool = new ThreadPoolExecutor(5, 20, 10, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(10000), userBalanceFactory, getCallerRunsPolicyExecutionHandler());


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
