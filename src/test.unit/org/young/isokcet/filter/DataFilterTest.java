/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.filter;

import java.io.IOException;

import junit.framework.Assert;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.TestFilterChainContextFactory;
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
public class DataFilterTest {

    private static ClientDataFilter cfilter;
    private static ServerDataFilter sfilter;

    private static ParserFactory parserFactory;

    //    private static IMockBuilder<FilterChainContext> mockBuilder = EasyMock.createMockBuilder(FilterChainContext.class);
    //
    //    private static FilterChainContext mockCtx;

    @BeforeClass
    public static void beforeClass() {
        cfilter = new ClientDataFilter();
        sfilter = new ServerDataFilter();
        parserFactory = new ParserFactory();

        //        mockCtx = mockBuilder.createMock();
    }

    @AfterClass
    public static void afterClass() {
        cfilter = null;
        sfilter = null;
        parserFactory = null;
        //        mockCtx = null;
    }

    @Test
    public void testHandleReadSucess() {
        FilterChainContext ctx = new TestFilterChainContextFactory().getFilterChainContext();

        ServiceResponse message = new ServiceResponse();
        message.setId("11111111111111111111111111111122");
        message.setResponseCode(0);
        message.setResponseObject("test string");
        message.setTransformType(SocketKeys.TRANSFORM_JSON);
        message.setServiceId("0000000001");

        try {
            ctx.setMessage(message);
            sfilter.handleWrite(ctx);
            cfilter.handleRead(ctx);
            Assert.assertTrue(ctx.getMessage() instanceof ServiceResponse);
            ServiceResponse message2 = ctx.getMessage();
            Assert.assertEquals(message2.getId(), message.getId());
            Assert.assertEquals(message2.getResponseCode(), message.getResponseCode());
            Assert.assertEquals(message2.getResponseObject(), message.getResponseObject());

        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }

    }

    @Test
    public void testHandleReadError() {
        FilterChainContext ctx = new TestFilterChainContextFactory().getFilterChainContext();
        ServiceResponse message = new ServiceResponse();
        message.setId("11111111111111111111111111111122");
        message.setResponseCode(1);
        message.setResponseMessage("test error");
        message.setTransformType(SocketKeys.TRANSFORM_JSON);
        message.setServiceId("0000000001");

        try {
            ctx.setMessage(message);
            sfilter.handleWrite(ctx);
            cfilter.handleRead(ctx);

            Assert.assertTrue((ctx.getMessage()) instanceof ServiceResponse);

            ServiceResponse message2 = ctx.getMessage();
            Assert.assertEquals(message2.getId(), message.getId());
            Assert.assertEquals(message2.getResponseCode(), message.getResponseCode());
            Assert.assertEquals(message2.getResponseMessage(), message.getResponseMessage());

        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }

    }

    @Test
    public void testHandleWrite() {
        TestFilterChainContextFactory f = new TestFilterChainContextFactory();
        FilterChainContext ctx = f.getFilterChainContext();
        ServiceRequest message1 = new ServiceRequest();
        message1.setId("11111111111111111111111111111122");
        message1.setRequestObject("test string");
        message1.setServiceId("1000010001");
        message1.setTransformType(SocketKeys.TRANSFORM_JSON);
        ctx.setMessage(message1);

        try {

            cfilter.handleWrite(ctx);

            Assert.assertTrue(ctx.getMessage() instanceof Buffer);

            sfilter.handleRead(ctx);

            ServiceRequest message = ctx.getMessage();

            Assert.assertEquals(message1.getId(), message.getId());
            Assert.assertEquals(message1.getServiceId(), message.getServiceId());
            Assert.assertEquals(message1.getRequestObject(), message.getRequestObject());
            Assert.assertEquals(message1.getTransformType(), message.getTransformType());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

    }
}
