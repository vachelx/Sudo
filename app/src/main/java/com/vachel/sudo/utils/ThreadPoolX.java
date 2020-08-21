package com.vachel.sudo.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by jianglixuan on 2020/8/21.
 * Describe:
 */
public class ThreadPoolX {
    private static ThreadPoolExecutor sThreadPool; // 单列的线程池对象。

    private ThreadPoolX() {
    }

    public static ThreadPoolExecutor getThreadPool() {
        if (sThreadPool == null) {
            synchronized (ThreadPoolX.class) {
                if (sThreadPool == null) {
                    int corePoolSize = Runtime.getRuntime().availableProcessors() - 1;
                    int maximumPoolSize = corePoolSize * 2;
                    long keepAliveTime = 15 * 1000;
                    sThreadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>(), Executors.defaultThreadFactory());
                }
            }
        }
        return sThreadPool;
    }

    public void execute(Runnable runnable) {
        if (runnable == null || sThreadPool == null) {
            return;
        }
        sThreadPool.execute(runnable);
    }

    public void cancel(Runnable runnable) {
        if (sThreadPool != null) {
            sThreadPool.getQueue().remove(runnable);
        }
    }
}
