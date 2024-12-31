package com.open.ai.eros.pay.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * @类名：ThreadPoolConfig
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/6 21:29
 */

@Slf4j
@Configuration
public class ThreadPoolConfig {


    public static RejectedExecutionHandler getCallerRunsPolicyExecutionHandler() {
        return new ThreadPoolExecutor.CallerRunsPolicy() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                super.rejectedExecution(r, executor);
                log.warn("CallerRunsPolicy was rejected, runnable:{}`executor:{}`taskCount:{}", r.toString(), executor.toString(), executor.getTaskCount());
            }
        };
    }

    @Bean(name = "OrderExecutor")
    public Executor taskExecutor() {

        ThreadFactory orderFactory = new ThreadFactoryBuilder()
                .setNameFormat("order-pool-%d").build();
        return new ThreadPoolExecutor(2, 50, 10, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(10000), orderFactory, getCallerRunsPolicyExecutionHandler());
    }


}
