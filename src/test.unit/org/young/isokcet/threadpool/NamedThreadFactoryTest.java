/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.threadpool;

import junit.framework.Assert;

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
public class NamedThreadFactoryTest {

    private static NamedThreadFactory f;

    @BeforeClass
    public static void beforeClass() {
        f = new NamedThreadFactory();
    }

    @AfterClass
    public static void afterClass() {
        f = null;
    }

    @Test
    public void testNewThread() {
        try {
            f.newThread(new Runnable() {
                public void run() {

                }
            });
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCreateWithoutParameters() {
        try {
            new NamedThreadFactory("test", true);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCreate() {
        try {
            new NamedThreadFactory();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

}
