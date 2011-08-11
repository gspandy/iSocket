/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.filter;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.EmptyCompletionHandler;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChain;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.glassfish.grizzly.impl.SafeFutureImpl;
import org.glassfish.grizzly.ssl.SSLFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class ClientSSLFilter extends BaseFilter {

    private static final Logger logger = LoggerFactory.getLogger(ClientSSLFilter.class);

    private final SSLFilter sslFilter;

    private static final String MESSAGE = "Hello World!";

    private SafeFutureImpl<SSLEngineResult.HandshakeStatus> resultFuture;

    public ClientSSLFilter(SSLFilter sslFilter) {
        this.sslFilter = sslFilter;
        resultFuture = SafeFutureImpl.create();
    }

    /**
     * Handle newly connected {@link Connection}, perform SSL handshake and
     * send greeting message to a server.
     *
     * @param ctx {@link FilterChain} context
     * @return nextAction
     * @throws IOException
     */
    @Override
    public NextAction handleConnect(FilterChainContext ctx) throws IOException {
        final Connection connection = ctx.getConnection();

        //         Execute async SSL handshake
        sslFilter.handshake(connection, new EmptyCompletionHandler<SSLEngine>() {

            /**
             * Once SSL handshake will be completed - send greeting message
             */
            @Override
            public void completed(SSLEngine result) {
                try {
                    // Here we send String directly
                    //connection.write(MESSAGE);
                    if (logger.isDebugEnabled()) {
                        logger.debug("handshake status:{}", result.getHandshakeStatus());
                    }
                    resultFuture.result(result.getHandshakeStatus());
                } catch (Exception e) {
                    try {
                        connection.close();
                    } catch (IOException ex) {
                    }
                }
            }
        });

        return ctx.getInvokeAction();
    }

    public HandshakeStatus getHandshakeStatus(long timeout, TimeUnit unit) throws IOException {
        try {
            return resultFuture.get(timeout, unit);
        } catch (InterruptedException e) {
            throw new IOException(e);
        } catch (ExecutionException e) {
            throw new IOException(e);
        } catch (TimeoutException e) {
            return null;
        } finally {
            this.resultFuture.recycle(true);
        }
    }

    public HandshakeStatus getHandshakeStatus() throws IOException {
        try {
            return resultFuture.get();
        } catch (InterruptedException e) {
            throw new IOException(e);
        } catch (ExecutionException e) {
            throw new IOException(e);
        } finally {
            this.resultFuture.recycle(true);
        }
    }

}
