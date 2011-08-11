/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.threadpool;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

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
public class JobDispatcherTest {
    private static JobDispatcher dispatcher;
    private static AtomicInteger count = new AtomicInteger();

    @BeforeClass
    public static void beforeClass() {
        dispatcher = new JobDispatcher();
    }

    @AfterClass
    public static void afterClass() {
        dispatcher.stopDispatcher();
        dispatcher = null;
    }

    @Test
    public void testExecute() {
        final long b = System.currentTimeMillis();
        final Random r = new Random();

        for (int i = 0; i < 10; i++) {
            dispatcher.execute(new Job() {
                public String getKey() {
                    return "" + (Math.abs(r.nextInt()) % 4);
                }

                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    long e = System.currentTimeMillis();
                    System.out.println(Thread.currentThread().getName() + "-key" + getKey() + ":" + (e - b));
                }
            });

        }

        System.out.println(dispatcher.getCurrentThreadStatus());

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
