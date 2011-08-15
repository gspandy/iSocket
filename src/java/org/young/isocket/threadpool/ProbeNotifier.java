/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isocket.threadpool;

import org.young.isocket.jmx.JobDispatcherProbe;

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
final class ProbeNotifier {
    /**

    /**
     * Notify registered {@link jobDispatcherProbe}s about the "thread pool started" event.
     *
     * @param jobDispatcher the {@link AbstractjobDispatcher} being monitored
     */
    static void notifyThreadPoolStarted(final JobDispatcher jobDispatcher) {

        final JobDispatcherProbe[] probes = jobDispatcher.monitoringConfig.getProbesUnsafe();
        if (probes != null) {
            for (JobDispatcherProbe probe : probes) {
                probe.onThreadPoolStartEvent(jobDispatcher);
            }
        }
    }

    /**
     * Notify registered {@link jobDispatcherProbe}s about the "thread pool stopped" event.
     *
     * @param jobDispatcher the {@link AbstractjobDispatcher} being monitored
     */
    static void notifyThreadPoolStopped(final JobDispatcher jobDispatcher) {

        final JobDispatcherProbe[] probes = jobDispatcher.monitoringConfig.getProbesUnsafe();
        if (probes != null) {
            for (JobDispatcherProbe probe : probes) {
                probe.onThreadPoolStopEvent(jobDispatcher);
            }
        }
    }

    /**
     * Notify registered {@link jobDispatcherProbe}s about the "thread allocated" event.
     *
     * @param jobDispatcher the {@link AbstractjobDispatcher} being monitored
     * @param thread the thread that has been allocated
     */
    static void notifyThreadAllocated(final JobDispatcher jobDispatcher, final Job thread) {

        final JobDispatcherProbe[] probes = jobDispatcher.monitoringConfig.getProbesUnsafe();
        if (probes != null) {
            for (JobDispatcherProbe probe : probes) {
                probe.onThreadAllocateEvent(jobDispatcher, thread);
            }
        }
    }

    /**
     * Notify registered {@link jobDispatcherProbe}s about the "thread released" event.
     *
     * @param jobDispatcher the {@link AbstractjobDispatcher} being monitored
     * @param thread the thread that has been allocated
     */
    static void notifyThreadReleased(final JobDispatcher jobDispatcher, final Job thread) {

        final JobDispatcherProbe[] probes = jobDispatcher.monitoringConfig.getProbesUnsafe();
        if (probes != null) {
            for (JobDispatcherProbe probe : probes) {
                probe.onThreadReleaseEvent(jobDispatcher, thread);
            }
        }
    }

    /**
     * Notify registered {@link jobDispatcherProbe}s about the "max number of threads reached" event.
     *
     * @param jobDispatcher the {@link AbstractjobDispatcher} being monitored
     * @param maxNumberOfThreads the maximum number of threads allowed in the
     *  {@link AbstractjobDispatcher}
     */
    static void notifyMaxNumberOfThreads(final JobDispatcher jobDispatcher, final int maxNumberOfThreads) {

        final JobDispatcherProbe[] probes = jobDispatcher.monitoringConfig.getProbesUnsafe();
        if (probes != null) {
            for (JobDispatcherProbe probe : probes) {
                probe.onMaxNumberOfThreadsEvent(jobDispatcher, maxNumberOfThreads);
            }
        }
    }

    /**
     * Notify registered {@link jobDispatcherProbe}s about the "task queued" event.
     *
     * @param jobDispatcher the {@link AbstractjobDispatcher} being monitored
     * @param task a unit of work to be processed
     */
    static void notifyTaskQueued(final JobDispatcher jobDispatcher, final Runnable task) {

        final JobDispatcherProbe[] probes = jobDispatcher.monitoringConfig.getProbesUnsafe();
        if (probes != null) {
            for (JobDispatcherProbe probe : probes) {
                probe.onTaskQueueEvent(jobDispatcher, task);
            }
        }
    }

    /**
     * Notify registered {@link jobDispatcherProbe}s about the "task dequeued" event.
     *
     * @param jobDispatcher the {@link AbstractjobDispatcher} being monitored
     * @param task a unit of work to be processed
     */
    static void notifyTaskDequeued(final JobDispatcher jobDispatcher, final Runnable task) {

        final JobDispatcherProbe[] probes = jobDispatcher.monitoringConfig.getProbesUnsafe();
        if (probes != null) {
            for (JobDispatcherProbe probe : probes) {
                probe.onTaskDequeueEvent(jobDispatcher, task);
            }
        }
    }

    //    /**
    //     * Notify registered {@link jobDispatcherProbe}s about the "task completed" event.
    //     *
    //     * @param jobDispatcher the {@link AbstractjobDispatcher} being monitored
    //     * @param task a unit of work to be processed
    //     */
    //    static void notifyTaskCompleted(final JobDispatcher jobDispatcher, final Runnable task) {
    //
    //        final JobDispatcherProbe[] probes = jobDispatcher.monitoringConfig.getProbesUnsafe();
    //        if (probes != null) {
    //            for (JobDispatcherProbe probe : probes) {
    //                probe.onTaskCompleteEvent(jobDispatcher, task);
    //            }
    //        }
    //    }

    /**
     * Notify registered {@link jobDispatcherProbe}s about the "task queue overflow" event.
     *
     * @param jobDispatcher the {@link AbstractjobDispatcher} being monitored
     */
    static void notifyTaskQueueOverflow(final JobDispatcher jobDispatcher) {

        final JobDispatcherProbe[] probes = jobDispatcher.monitoringConfig.getProbesUnsafe();
        if (probes != null) {
            for (JobDispatcherProbe probe : probes) {
                probe.onTaskQueueOverflowEvent(jobDispatcher);
            }
        }
    }

}
