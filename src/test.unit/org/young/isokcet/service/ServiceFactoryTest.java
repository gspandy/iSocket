/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.service;

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
public class ServiceFactoryTest {

    private static ServiceFactory sf;

    @BeforeClass
    public static void beforeClass() {
        try {
            sf = ServiceFactory.getInstance();
        } catch (Exception e) {
            Assert.fail("create ServiceFactory error:" + e.getMessage());
        }
    }

    @AfterClass
    public static void afterClass() {
        sf = null;
    }

    @Test
    public void testDoStartElement() {
        ISocketService s1 = sf.getService("1000010001");
        Assert.assertNotNull(s1);
        ISocketService s2 = sf.getService("1000010002");
        Assert.assertNotNull(s2);
    }
}
