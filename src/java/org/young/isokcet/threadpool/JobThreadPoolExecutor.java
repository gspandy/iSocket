/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 
 * 扩展线程池基本实现， 在每个任务结束后需要减去任务计数器的值
 * 
 * @author yangjun1120@gmail.com
 */
public class JobThreadPoolExecutor extends ThreadPoolExecutor {

    private JobDispatcher jobDispatcher;

    public JobThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
            BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, JobDispatcher jobDispatcher) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        this.jobDispatcher = jobDispatcher;
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        if (r instanceof Job) {
            jobDispatcher.releaseJob((Job) r);

        }
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        if (r instanceof Job) {
            jobDispatcher.beforeExecuteJob((Job) r);

        }

    }

}
