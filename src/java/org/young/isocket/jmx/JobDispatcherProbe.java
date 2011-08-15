/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isocket.jmx;

import org.glassfish.grizzly.threadpool.AbstractThreadPool;
import org.young.isocket.threadpool.Job;
import org.young.isocket.threadpool.JobDispatcher;

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
public interface JobDispatcherProbe {

    /**
     * <p>
     * This event may be fired when an {@link AbstractThreadPool} implementation
     * starts running.
     * </p>
     *
     * @param threadPool the {@link AbstractThreadPool} being monitored
     */
    public void onThreadPoolStartEvent(JobDispatcher jobDispatcher);

    /**
     * <p>
     * This event may be fired when an {@link AbstractThreadPool} implementation
     * stops.
     * </p>
     *
     * @param threadPool the {@link AbstractThreadPool} being monitored
     */
    public void onThreadPoolStopEvent(JobDispatcher jobDispatcher);

    /**
     * <p>
     * This event may be fired when an {@link AbstractThreadPool} implementation
     * allocates a new managed {@link Thread}.
     * </p>
     *
     * @param threadPool the {@link AbstractThreadPool} being monitored
     * @param thread the thread that has been allocated
     */
    public void onThreadAllocateEvent(JobDispatcher jobDispatcher, Job thread);

    /**
     * <p>
     * This event may be fired when a thread will no longer be managed by the
     * {@link AbstractThreadPool} implementation.
     * </p>
     *
     * @param threadPool the {@link AbstractThreadPool} being monitored
     * @param thread the thread that is no longer being managed by the
     *  {@link AbstractThreadPool}
     */
    public void onThreadReleaseEvent(JobDispatcher jobDispatcher, Job thread);

    /**
     * <p>
     * This event may be fired when the {@link AbstractThreadPool} implementation
     * has allocated and is managing a number of threads equal to the maximum limit
     * of the pool.
     * <p>
     *
     * @param threadPool the {@link AbstractThreadPool} being monitored
     * @param maxNumberOfThreads the maximum number of threads allowed in the
     *  {@link AbstractThreadPool}
     */
    public void onMaxNumberOfThreadsEvent(JobDispatcher jobDispatcher, int maxNumberOfThreads);

    /**
     * <p>
     * This event may be fired when a task has been queued for processing.
     * </p>
     *
     * @param threadPool the {@link AbstractThreadPool} being monitored
     * @param task a unit of work to be processed
     */
    public void onTaskQueueEvent(JobDispatcher jobDispatcher, Runnable task);

    /**
     * <p>
     * This event may be fired when a task has been pulled from the queue and
     * is about to be processed.
     *
     * @param threadPool the {@link AbstractThreadPool} being monitored
     * @param task a unit of work that is about to be processed.
     */
    public void onTaskDequeueEvent(JobDispatcher jobDispatcher, Runnable task);

    /**
     * <p>
     * This event may be fired when a dequeued task has completed processing.
     * </p>
     *
     * @param threadPool the {@link AbstractThreadPool} being monitored
     * @param task the unit of work that has completed processing
     */
    // public void onTaskCompleteEvent(JobDispatcher jobDispatcher, Runnable task);

    /**
     * <p>
     * This event may be fired when the task queue of the {@link AbstractThreadPool}
     * implementation has exceeded its configured size.
     * </p>
     *
     * @param threadPool the {@link AbstractThreadPool} being monitored
     */
    public void onTaskQueueOverflowEvent(JobDispatcher jobDispatcher);

}
