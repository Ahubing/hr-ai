package com.open.ai.eros.social.email.manager;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class ThreadPoolManager {


    private static ThreadFactory sendEmailThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("email-pool-%d").build();
    public static ThreadPoolExecutor sendEmailPool = new ThreadPoolExecutor(1, 2, 10, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(100), sendEmailThreadFactory, getCallerRunsPolicyExecutionHandler());


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
