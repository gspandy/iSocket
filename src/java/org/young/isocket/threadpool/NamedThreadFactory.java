/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isocket.threadpool;

/**
 * <p>
 * 描述:可命名的线程工厂类
 * </p>
 *
 * @see
 * @author yangjun2
 * @email yangjun1120@gmail.com
 *
 */

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 可命名的线程池工厂类
 * @author fangweng
 * 
 */
public class NamedThreadFactory implements ThreadFactory {
    static final AtomicInteger poolNumber = new AtomicInteger(1);

    final AtomicInteger threadNumber = new AtomicInteger(1);
    final ThreadGroup group;
    final String namePrefix;
    final boolean isDaemon;

    public NamedThreadFactory() {
        this("pool");
    }

    public NamedThreadFactory(String name) {
        this(name, false);
    }

    public NamedThreadFactory(String prefix, boolean daemon) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = prefix + "-" + poolNumber.getAndIncrement() + "-thread-";
        isDaemon = daemon;
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        t.setDaemon(isDaemon);
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;

    }
}
