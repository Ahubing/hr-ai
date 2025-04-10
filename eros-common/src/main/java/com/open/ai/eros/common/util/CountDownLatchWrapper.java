package com.open.ai.eros.common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CountDownLatchWrapper {

    /**
     * 线程池
     */
    private ExecutorService executorService;

    /**
     * 超时秒数
     */
    private Integer timeoutSeconds;

    private CountDownLatch latch;

    public CountDownLatchWrapper(ExecutorService executorService, Integer timeoutSeconds, Integer count) {
        this.executorService = executorService;
        this.timeoutSeconds = timeoutSeconds;
        this.latch = new CountDownLatch(count);
    }

    public void submit(Task task) {
        try {
            executorService.submit(()->{
                try {
                    task.doTask();
                } catch (Exception e) {
                    log.error("CountDownLatchWrapper error:", e);
                } finally {
                    // 任务完成后，计数减一
                    latch.countDown();
                }
            });
        } catch (Exception we) {
            log.error("CountDownLatchWrapper submit error:", we);
            latch.countDown();
        }
    }

    public boolean await(){
        try {
            // 等待所有任务完成，最好设置超时时间
            return latch.await(timeoutSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("CountDownLatchWrapper timeout:", e);
        }
        return false;
    }

    @FunctionalInterface
    public interface Task{
        void doTask();
    }

}
