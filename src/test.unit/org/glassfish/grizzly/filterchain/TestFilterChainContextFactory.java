/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.glassfish.grizzly.filterchain;

import java.nio.channels.ServerSocketChannel;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.memory.MemoryManager;
import org.glassfish.grizzly.nio.transport.TCPNIOServerConnection;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;

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
public class TestFilterChainContextFactory {

    public FilterChainContext getFilterChainContext() {
        FilterChainContext ctx = new FilterChainContext();
        TCPNIOTransport transport = TCPNIOTransportBuilder.newInstance().build();
        transport.setMemoryManager(MemoryManager.DEFAULT_MEMORY_MANAGER);
        Connection conn = new TCPNIOServerConnection(transport, (ServerSocketChannel) null);
        ctx.setConnection(conn);
        return ctx;
    }

}
