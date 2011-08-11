/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.jmx;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.glassfish.gmbal.Description;
import org.glassfish.gmbal.GmbalMBean;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedObject;
import org.glassfish.grizzly.monitoring.jmx.GrizzlyJmxManager;
import org.glassfish.grizzly.monitoring.jmx.JmxObject;
import org.young.isokcet.threadpool.Job;
import org.young.isokcet.threadpool.JobDispatcher;

/**
 * <p>
 * 
 * </p>
 *
 * @see
 * @author yangjun2
 * @email yangjun1120@gmail.com
 *
 */

@ManagedObject
@Description("JobDispatcher (Custom ThreadPool).")
public class JobDispatcherJMX extends JmxObject {

    private final JobDispatcher jobDispatcher;
    private final JobDispatcherProbe probe = new JMXJobDispatcherProbe();

    private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicInteger currentAllocatedThreadCount = new AtomicInteger();
    private final AtomicInteger totalAllocatedThreadCount = new AtomicInteger();
    private final AtomicInteger currentQueuedTasksCount = new AtomicInteger();
    //private final AtomicLong totalCompletedTasksCount = new AtomicLong();
    private final AtomicInteger totalTaskQueueOverflowCount = new AtomicInteger();

    public JobDispatcherJMX(JobDispatcher jobDispatcher) {
        this.jobDispatcher = jobDispatcher;
        //this.jobDispatcher.getMonitoringConfig().addProbes(probe);
    }

    /**
     * @return the Java type of the managed thread pool.
     */
    @ManagedAttribute(id = "thread-pool-type")
    @Description("The Java type of the thread pool implementation being used.")
    public String getPoolType() {
        return jobDispatcher.getClass().getName();
    }

    /**
     * @return <code>true</code> if this pool has been started, otherwise return
     *  <code>false</code>
     */
    @ManagedAttribute(id = "thread-pool-started")
    @Description("Indiciates whether or not the thread pool has been started.")
    public boolean isStarted() {
        return started.get();
    }

    /**
     * @return the max number of threads allowed by this thread pool.
     */
    @ManagedAttribute(id = "thread-pool-max-num-threads")
    @Description("The maximum number of the threads allowed by this thread pool.")
    public int getMaxAllowedThreads() {
        return jobDispatcher.getMaximumPoolSize();
    }

    @ManagedAttribute(id = "thread-core-pool-size")
    @Description("The number of tasks currently being processed by this thread pool.")
    public int getCorePoolSize() {
        return jobDispatcher.getCorePoolSize();
    }

    /**
     * @return the current number of threads maintained by this thread pool.
     */
    @ManagedAttribute(id = "thread-pool-allocated-thread-count")
    @Description("The current number of threads managed by this thread pool.")
    public int getCurrentAllocatedThreadCount() {
        return currentAllocatedThreadCount.get();
    }

    /**
     * @return the total number of threads that have been allocated over time
     *  by this thread pool.
     */
    @ManagedAttribute(id = "thread-pool-total-allocated-thread-count")
    @Description("The total number of threads allocated during the lifetime of this thread pool.")
    public int getTotalAllocatedThreadCount() {
        return totalAllocatedThreadCount.get();
    }

    /**
     * @return the current number of tasks that have been queued for processing
     *  by this thread pool.
     */
    @ManagedAttribute(id = "thread-pool-queued-task-count")
    @Description("The number of tasks currently being processed by this thread pool.")
    public int getCurrentTaskCount() {
        return currentQueuedTasksCount.get();
    }

    //    /**
    //     * @return the total number of tasks that have been completed by this
    //     *  thread pool.
    //     */
    //    @ManagedAttribute(id = "thread-pool-total-completed-tasks-count")
    //    @Description("The total number of tasks that have been processed by this thread pool.")
    //    public long getTotalCompletedTasksCount() {
    //        return totalCompletedTasksCount.get();
    //    }

    /**
     * @return the number of times the task queue has reached it's upper limit.
     */
    @ManagedAttribute(id = "thread-pool-task-queue-overflow-count")
    @Description("The total number of times the task queue of this thread pool has been saturated.")
    public int getTotalTaskQueueOverflowCount() {
        return totalTaskQueueOverflowCount.get();
    }

    @Override
    public String getJmxName() {
        return "JobDispatcher";
    }

    @Override
    protected void onDeregister(GrizzlyJmxManager mom) {
        jobDispatcher.getMonitoringConfig().removeProbes(probe);
    }

    @Override
    protected void onRegister(GrizzlyJmxManager mom, GmbalMBean bean) {
        jobDispatcher.getMonitoringConfig().addProbes(probe);
    }

    private class JMXJobDispatcherProbe implements JobDispatcherProbe {

        @Override
        public void onThreadPoolStartEvent(JobDispatcher jobDispatcher) {
            started.compareAndSet(false, true);
        }

        @Override
        public void onThreadPoolStopEvent(JobDispatcher jobDispatcher) {
            started.compareAndSet(true, false);
        }

        @Override
        public void onThreadAllocateEvent(JobDispatcher jobDispatcher, Job thread) {
            currentAllocatedThreadCount.incrementAndGet();
            totalAllocatedThreadCount.incrementAndGet();

        }

        @Override
        public void onThreadReleaseEvent(JobDispatcher jobDispatcher, Job thread) {
            currentAllocatedThreadCount.decrementAndGet();

        }

        @Override
        public void onMaxNumberOfThreadsEvent(JobDispatcher jobDispatcher, int maxNumberOfThreads) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTaskQueueEvent(JobDispatcher jobDispatcher, Runnable task) {
            currentQueuedTasksCount.incrementAndGet();
        }

        @Override
        public void onTaskDequeueEvent(JobDispatcher jobDispatcher, Runnable task) {
            currentQueuedTasksCount.decrementAndGet();

        }

        //        @Override
        //        public void onTaskCompleteEvent(JobDispatcher jobDispatcher, Runnable task) {
        //            // TODO Auto-generated method stub
        //
        //        }

        @Override
        public void onTaskQueueOverflowEvent(JobDispatcher jobDispatcher) {
            totalTaskQueueOverflowCount.incrementAndGet();

        }

    }

}
