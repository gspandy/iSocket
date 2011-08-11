/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.server;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;

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
public class NIOSocketServerTest {

    private static NIOSocketServer server;

    @BeforeClass
    public static void beforeClass() {
        server = NIOSocketServer.getInstance();
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("server start error:" + e.getMessage());
        }
    }

    @AfterClass
    public static void afterClass() {
        server = null;
    }

}
