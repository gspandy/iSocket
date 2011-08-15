/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isocket.filter;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.glassfish.grizzly.impl.FutureImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.young.isocket.service.ServiceResponse;

/**
 * <p>
 *  获取结果Filter
 * </p>
 *
 * @see
 * @author yangjun2
 * @email yangjun1120@gmail.com
 *
 */
public class ClientFutureFilter extends BaseFilter {

    private static final Logger logger = LoggerFactory.getLogger(ClientFutureFilter.class);

    private FutureImpl<ServiceResponse> resultFuture;

    public ClientFutureFilter() {
    }

    public void setFuture(FutureImpl<ServiceResponse> resultFuture) {
        this.resultFuture = resultFuture;
    }

    public ServiceResponse getServiceResponse(long timeout, TimeUnit unit) throws IOException {
        try {
            return this.resultFuture.get(timeout, unit);
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

    public ServiceResponse getServiceResponse() throws IOException {
        try {
            return this.resultFuture.get();
        } catch (InterruptedException e) {
            throw new IOException(e);
        } catch (ExecutionException e) {
            throw new IOException(e);
        } finally {
            this.resultFuture.recycle(true);
        }

    }

    @Override
    public NextAction handleRead(FilterChainContext ctx) throws IOException {
        final ServiceResponse message = ctx.getMessage();
        resultFuture.result(message);

        return ctx.getStopAction();
    }

}
