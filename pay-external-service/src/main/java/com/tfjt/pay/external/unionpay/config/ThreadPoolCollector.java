package com.tfjt.pay.external.unionpay.config;

import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolCollector {

    private static final String THREAD_POOL_CORE_SIZE = "thread_pool_core_size";
    private static final String THREAD_POOL_LARGEST_SIZE = "thread_pool_largest_size";
    private static final String THREAD_POOL_MAX_SIZE = "thread_pool_max_size";
    private static final String THREAD_POOL_ACTIVE_SIZE = "thread_pool_active_size";
    private static final String THREAD_POOL_THREAD_COUNT = "thread_pool_thread_count";
    private static final String THREAD_POOL_QUEUE_SIZE = "thread_pool_queue_size";


    public static void registerThreadPool(String pooName,ThreadPoolExecutor executor){
        List<Tag> tags=Arrays.asList(new ImmutableTag("poolName",pooName));
        Metrics.gauge(THREAD_POOL_CORE_SIZE,tags,executor,ThreadPoolExecutor::getCorePoolSize);
        Metrics.gauge(THREAD_POOL_LARGEST_SIZE,tags,executor,ThreadPoolExecutor::getLargestPoolSize);
        Metrics.gauge(THREAD_POOL_MAX_SIZE,tags,executor,ThreadPoolExecutor::getMaximumPoolSize);
        Metrics.gauge(THREAD_POOL_ACTIVE_SIZE,tags,executor,ThreadPoolExecutor::getActiveCount);
        Metrics.gauge(THREAD_POOL_THREAD_COUNT,tags,executor,ThreadPoolExecutor::getPoolSize);
        Metrics.gauge(THREAD_POOL_QUEUE_SIZE,tags,executor,s->s.getQueue().size());
    }


    public static void registerThreadPool(String pooName,ThreadPoolTaskExecutor executorTask){
        List<Tag> tags=Arrays.asList(new ImmutableTag("poolName",pooName));
        Metrics.gauge(THREAD_POOL_CORE_SIZE,tags,executorTask,ThreadPoolTaskExecutor::getCorePoolSize);
        Metrics.gauge(THREAD_POOL_MAX_SIZE,tags,executorTask,ThreadPoolTaskExecutor::getMaxPoolSize);
        Metrics.gauge(THREAD_POOL_ACTIVE_SIZE,tags,executorTask,ThreadPoolTaskExecutor::getActiveCount);
        Metrics.gauge(THREAD_POOL_THREAD_COUNT,tags,executorTask,ThreadPoolTaskExecutor::getPoolSize);
        Metrics.gauge(THREAD_POOL_QUEUE_SIZE,tags,executorTask,s->s.getThreadPoolExecutor().getQueue().size());
    }


}
