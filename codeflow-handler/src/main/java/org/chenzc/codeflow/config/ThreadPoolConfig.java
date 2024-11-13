package org.chenzc.codeflow.config;

import cn.hutool.core.thread.ExecutorBuilder;
import lombok.NoArgsConstructor;
import org.chenzc.codeflow.constant.ThreadPoolConstant;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor
public class ThreadPoolConfig {
    public static ExecutorService getExecutor() {
        return ExecutorBuilder.create()
                .setCorePoolSize(ThreadPoolConstant.COMMON_CORE_POOL_SIZE)
                .setMaxPoolSize(ThreadPoolConstant.COMMON_MAX_POOL_SIZE)
                .setWorkQueue(new LinkedBlockingQueue<>(ThreadPoolConstant.COMMON_QUEUE_SIZE))
                .setHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                .setAllowCoreThreadTimeOut(true)
                .setKeepAliveTime(ThreadPoolConstant.COMMON_KEEP_LIVE_TIME, TimeUnit.SECONDS)
                .build();
    }
}
