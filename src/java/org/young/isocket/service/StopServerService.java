/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isocket.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.young.isocket.server.NIOSocketServer;

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
public class StopServerService extends AbstractService {
    private static final Logger logger = LoggerFactory.getLogger(StopServerService.class);

    @Override
    public void doInvokeBefore(ServiceRequest svcReq) {

    }

    @Override
    public void doInvokeAfter(ServiceRequest svcReq, Object obj) {

    }

    @Override
    public Object doInvoke(ServiceRequest svcReq) {

        Thread t = new Thread() {
            public void run() {
                try {
                    Thread.sleep(5 * 1000);
                    NIOSocketServer.getInstance().stop();
                    NIOSocketServer.getInstance().notifyServer();
                } catch (Exception e) {
                    logger.error("shutdown server error.", e);
                }
            }
        };

        t.start();

        return "ShutDown Server OK!";
    }

    public static void main(String[] args) {
    }

}
