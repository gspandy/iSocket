/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isocket.jmx;

import org.glassfish.gmbal.Description;
import org.glassfish.gmbal.GmbalMBean;
import org.glassfish.gmbal.ManagedObject;
import org.glassfish.gmbal.ManagedOperation;
import org.glassfish.grizzly.monitoring.jmx.GrizzlyJmxManager;
import org.glassfish.grizzly.monitoring.jmx.JmxObject;
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

@ManagedObject
@Description("iSocketServer JMX Object.")
public class SocketServerJMX extends JmxObject {

    private final SocketServerProbe probe = new JMXSocketServerProbe();

    @Override
    public String getJmxName() {
        return "SocketServerMbean";
    }

    @Override
    protected void onDeregister(GrizzlyJmxManager arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onRegister(GrizzlyJmxManager arg0, GmbalMBean arg1) {
        // TODO Auto-generated method stub

    }

    public SocketServerJMX() {
    }

    /**
     * @return the Java type of the managed thread pool.
     */
    @ManagedOperation(id = "shutdown-server")
    @Description("shutdown socket server")
    public void stopServer() {
        probe.stopServer();
    }

    private class JMXSocketServerProbe implements SocketServerProbe {

        @Override
        public void stopServer() {
            try {
                NIOSocketServer.getInstance().stop();
                NIOSocketServer.getInstance().notifyServer();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
