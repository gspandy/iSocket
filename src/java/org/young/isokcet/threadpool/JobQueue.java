/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.young.icore.annotation.PropertiesAnnotation;
import org.young.icore.util.PropertiesLoaderUtils;

/**
 * <p>
 * 描述: 每一个不同的jobkey都会有默认的一套队列机制，
 *       用于较为高效的处理队列中的数据
 * </p>
 *
 * @see
 * @author yangjun2
 * @email yangjun1120@gmail.com
 *
 */
public class JobQueue {

    private static final Logger logger = LoggerFactory.getLogger(JobQueue.class);

    /**
     * 该类任务的主键
     */
    private String jobKey;

    /**
     * 队列最大的长度
     */
    private int maximumQueueSize;

    /**
     * 锁
     */
    private ReentrantLock lock;

    /**
     * 是否有外部线程可用的信号量
     */
    private Condition hasResource;

    /**
     * 当前队列是否有任务的信号量
     */
    private Condition hasJob;

    /**
     * 队列计数器
     */
    private AtomicInteger counter;

    /**
     * 检查队列的后台线程
     */
    private QueueChecker checker;

    /**
     * 内部队列
     */
    private BlockingQueue<Job> jobQueue;

    /**
     * 外部任务调度执行器
     */
    private JobDispatcher jobDispatcher;

    /**
     * 没有任务时sleep时间，单位：微秒
     */
    @PropertiesAnnotation(name = "nojobsleeptime", resource = "isocket-server.properties")
    private long noJobSleepTime = 10000;

    /**
     * 没有外部线程可用时sleep时间,单位：微秒
     */
    @PropertiesAnnotation(name = "noresourcesleeptime", resource = "isocket-server.properties")
    private long noReousrceSleepTime = 2000;

    public JobQueue(String jobKey, int maximumQueueSize, JobDispatcher jobDispatcher) {
        this.jobKey = jobKey;
        this.maximumQueueSize = maximumQueueSize;
        this.jobDispatcher = jobDispatcher;
        this.init();
    }

    public void init() {
        //propery init
        PropertiesLoaderUtils.setPropertiesFields(this);

        //other init
        lock = new ReentrantLock();
        hasResource = lock.newCondition();
        hasJob = lock.newCondition();
        counter = new AtomicInteger();
        jobQueue = new LinkedBlockingQueue<Job>(maximumQueueSize);

        checker = new QueueChecker();
        checker.setDaemon(true);
        checker.start();
    }

    public void clean() {
        counter.set(0);

        jobQueue.clear();
        checker.stopThread();
        checker = null;

    }

    public int size() {
        return counter.get();
    }

    /**
     * 任务尝试入队列
     * @param job
     * @return
     */
    public boolean offer(Job job) {
        boolean result = jobQueue.offer(job);

        if (result) {
            counter.incrementAndGet();
            notifyHasJob();
        }

        return result;
    }

    /**
     * 通知有任务已经入队列
     */
    public void notifyHasJob() {
        boolean flag = false;

        try {
            flag = lock.tryLock(100, TimeUnit.MILLISECONDS);

            if (flag) {
                hasJob.signalAll();
            }
        } catch (InterruptedException ie) {
            //do nothing;
        } catch (Exception ex) {
            logger.error("notifyHasJob error!", ex);
        } finally {
            if (flag) {
                lock.unlock();
            }
        }
    }

    /**
     * 通知外部有资源可以执行任务
     */
    public void notifyHasResource() {
        boolean flag = false;

        try {
            flag = lock.tryLock(100, TimeUnit.MILLISECONDS);

            if (flag)
                hasResource.signalAll();
        } catch (InterruptedException ie) {
            //do nothing;
        } catch (Exception ex) {
            logger.error("notifyHasResource error!", ex);
        } finally {
            if (flag)
                lock.unlock();
        }
    }

    /**
     * 队列中无任务的阻塞消息
     * @param waittime
     */
    public void blockNoJob(long waittime) {
        boolean flag = lock.tryLock();

        if (flag) {
            try {
                hasJob.await(waittime, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ie) {
                //do nothing;
            } catch (Exception ex) {
                logger.error("blockNoJob error!", ex);
            } finally {
                lock.unlock();
            }
        }

    }

    /**
     * 外部无可执行资源阻塞消息
     * @param waittime
     */
    public void blockNoResource(long waittime) {
        boolean flag = lock.tryLock();

        if (flag) {
            try {
                hasResource.await(waittime, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ie) {
                //do nothing;
            } catch (Exception ex) {
                logger.error("blockNoResource error!", ex);
            } finally {
                lock.unlock();
            }
        }
    }

    class QueueChecker extends Thread {
        private boolean isRunning = true;

        public QueueChecker() {
            super("queuechecker-" + jobKey);
        }

        public void run() {

            while (isRunning) {

                try {
                    //尝试获取队列中任务，由于只有一个线程读取，因此可以采用peek + 判断后的poll            
                    Job job = jobQueue.peek();

                    if (job != null) {
                        //判断是否有资源，并且会先并发减去资源
                        if (jobDispatcher.checkJobResource(job)) {
                            //如果有资源，弹出队列，执行任务，计数器递减
                            jobQueue.poll();
                            logger.debug("sumit queue job to threadpool!");
                            jobDispatcher.getThreadPool().execute(job);
                            counter.decrementAndGet();

                            ProbeNotifier.notifyTaskDequeued(jobDispatcher, job);
                        } else
                            blockNoResource(noReousrceSleepTime);//如果没有资源就阻塞2秒钟

                    } else {
                        blockNoJob(noJobSleepTime);//如果没有任务就阻塞10秒钟
                    }

                } catch (Exception ex) {
                    logger.error("QueueChecker run error!", ex);
                }

            }

        }

        public void stopThread() {
            try {
                isRunning = false;
                this.interrupt();
            } catch (Exception e) {
                logger.error("stopThread error!", e);
            }
        }
    }

}
