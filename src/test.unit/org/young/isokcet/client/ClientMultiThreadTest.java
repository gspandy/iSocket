/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.client;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.glassfish.grizzly.Connection;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.young.isokcet.service.ServiceRequest;
import org.young.isokcet.service.ServiceResponse;
import org.young.isokcet.util.SocketKeys;

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
public class ClientMultiThreadTest {

    private static NIOSocketClient client;
    private static Connection connection;

    @BeforeClass
    public static void beforeClass() {
        client = new NIOSocketClient("EAUSER123", "aaaaa888");
        client.setSoTimeout(1);
        client.setKeepAlive(true);
        client.setLinger(30);
        client.setTcpNoDelay(false);

        client.setNeedSSL(true);
        client.setTrustStoreFile("ssltest-cacerts.jks");
        client.setTrustStorePassword("changeit");
        client.setKeyStoreFile("ssltest-keystore.jks");
        client.setKeyStorePassword("changeit");

        try {
            client.connect("localhost", 7777);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

    }

    @AfterClass
    public static void afterClass() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        client = null;
    }

    @Test
    public void testMultiThreadConn() {
        final CountDownLatch cdAnswer = new CountDownLatch(1);
        for (int i = 0; i < 5; i++) {
            Thread t = new Thread() {

                @Override
                public void run() {
                    try {

                        ServiceRequest message1 = new ServiceRequest();
                        message1.setId("11111111111111111111111111111122");
                        message1.setRequestObject("" + Thread.currentThread().getName());
                        message1.setServiceId("1000010001");
                        message1.setTransformType(SocketKeys.TRANSFORM_JSON);
                        System.out.println(Thread.currentThread().getName() + " prepared!");
                        cdAnswer.await();
                        System.out.println(Thread.currentThread().getName() + " start!");
                        client.write(message1);
                        ServiceResponse rcvMessage = client.getServiceResponse(10, TimeUnit.SECONDS);
                        //ServiceResponse rcvMessage = client.getServiceResponse();
                        if (rcvMessage != null)
                            System.err.println(rcvMessage.toString() + ",Thread:" + Thread.currentThread().getName());
                        else
                            System.err.println("receiveMessage is null!");
                        //client.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Assert.fail(e.getMessage());
                    }
                }

            };

            t.start();

        }

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cdAnswer.countDown();

        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
