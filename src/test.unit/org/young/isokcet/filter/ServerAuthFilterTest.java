/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.filter;

import java.io.IOException;

import junit.framework.Assert;

import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.glassfish.grizzly.filterchain.TestFilterChainContextFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.young.isokcet.service.ServiceRequest;
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
public class ServerAuthFilterTest {
    private static ServerAuthFilter sfilter;

    private static ParserFactory parserFactory;

    @BeforeClass
    public static void beforeClass() {
        sfilter = new ServerAuthFilter();
        parserFactory = new ParserFactory();
    }

    @AfterClass
    public static void afterClass() {
        sfilter = null;
        parserFactory = null;
    }

    @Test
    public void testHandleReadAuthServiceSuccess() {
        TestFilterChainContextFactory f = new TestFilterChainContextFactory();
        FilterChainContext ctx = f.getFilterChainContext();
        ctx.getInternalContext().setProcessor(new NullProcessor());
        ServiceRequest message = new ServiceRequest();
        message.setServiceId(SocketKeys.SERVICE_ID_AUTH);

        ctx.setMessage(message);
        try {
            NextAction action = sfilter.handleRead(ctx);
            Assert.assertEquals(0, action.type());
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testHandleReadAuthSeviceFail() {
        TestFilterChainContextFactory f = new TestFilterChainContextFactory();
        FilterChainContext ctx = f.getFilterChainContext();
        ctx.getInternalContext().setProcessor(new NullProcessor());
        ServiceRequest message = new ServiceRequest();
        message.setServiceId("1");

        ctx.setMessage(message);
        try {
            NextAction action = sfilter.handleRead(ctx);
            Assert.assertEquals(1, action.type());
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    public void testHandleReadCommServiceSuccess() {
        TestFilterChainContextFactory f = new TestFilterChainContextFactory();
        FilterChainContext ctx = f.getFilterChainContext();
        ctx.getInternalContext().setProcessor(new NullProcessor());
        ServiceRequest message = new ServiceRequest();
        message.setServiceId(SocketKeys.SERVICE_ID_AUTH);

        ctx.setMessage(message);
        try {
            NextAction action = sfilter.handleRead(ctx);
            Assert.assertEquals(0, action.type());
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    public void testHandleReadCommServiceFail() {
        TestFilterChainContextFactory f = new TestFilterChainContextFactory();
        FilterChainContext ctx = f.getFilterChainContext();
        ctx.getInternalContext().setProcessor(new NullProcessor());
        ServiceRequest message = new ServiceRequest();
        message.setServiceId("1");

        ctx.setMessage(message);
        try {
            NextAction action = sfilter.handleRead(ctx);
            Assert.assertEquals(1, action.type());
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }
}
