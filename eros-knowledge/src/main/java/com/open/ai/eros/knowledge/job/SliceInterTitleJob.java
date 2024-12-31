package com.open.ai.eros.knowledge.job;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.open.ai.eros.ai.manager.ThreadPoolManager;
import com.open.ai.eros.common.service.RedisClient;
import com.open.ai.eros.common.util.CountDownLatchWrapper;
import com.open.ai.eros.db.constants.KnowledgeConstant;
import com.open.ai.eros.knowledge.manager.InferManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @类名：SliceInterQuestionJob
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/16 16:33
 */

@Component
@Slf4j
@EnableScheduling
public class SliceInterTitleJob {

    private static final int threadNum = 5;

    private static ThreadFactory sliceInterTitleThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("slice_inter_title-%d").build();
    public static ThreadPoolExecutor sliceInterTitlePool = new ThreadPoolExecutor(threadNum, threadNum, 10, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(10000), sliceInterTitleThreadFactory, ThreadPoolManager.getCallerRunsPolicyExecutionHandler());


    @Autowired
    private RedisClient redisClient;

    @Autowired
    private InferManager inferManager;

    @Scheduled(fixedDelay = 1000 )
    public void sliceInterTitle(){
        while (true){
            Set<String> spops = redisClient.spop(KnowledgeConstant.tileInferSliceSet,threadNum);
            if(CollectionUtils.isEmpty(spops)){
                return;
            }

            CountDownLatchWrapper countDownLatchWrapper = new CountDownLatchWrapper(sliceInterTitlePool, 3 * 1000000, spops.size());
            for (String spop : spops) {
                countDownLatchWrapper.submit(() -> {
                    try {
                        inferManager.inferSliceTitle(Long.parseLong(spop));
                    }catch (Exception e){
                        log.error("sliceInterQuestion spop={}",spop,e);
                    }
                });
            }
            countDownLatchWrapper.await();
        }
    }

}
