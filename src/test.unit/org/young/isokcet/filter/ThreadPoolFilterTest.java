/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.filter;

import java.io.IOException;

import junit.framework.Assert;

import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.young.isokcet.service.ServiceRequest;
import org.young.isokcet.threadpool.JobDispatcher;
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
public class ThreadPoolFilterTest {

    private static ThreadPoolFilter filter;

    private static ParserFactory parserFactory;

    //    private static IMockBuilder<FilterChainContext> mockBuilder = EasyMock.createMockBuilder(FilterChainContext.class);
    //
    //    private static FilterChainContext mockCtx;

    @BeforeClass
    public static void beforeClass() {
        JobDispatcher jobDispatcher = new JobDispatcher();
        filter = new ThreadPoolFilter(jobDispatcher);
        parserFactory = new ParserFactory();

        //        mockCtx = mockBuilder.createMock();
    }

    @AfterClass
    public static void afterClass() {
        filter = null;
        parserFactory = null;
        //        mockCtx = null;
    }

    @Test
    public void testHandleRead() {
        FilterChainContext ctx = new FilterChainContext();
        ServiceRequest message1 = new ServiceRequest();
        message1.setId("11111111111111111111111111111122");
        message1.setRequestObject("test string");
        message1.setServiceId("1000010001");
        message1.setTransformType(SocketKeys.TRANSFORM_JSON);
        ctx.setMessage(message1);
        try {
            filter.handleRead(ctx);
        } catch (IOException e) {
            //e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

}
