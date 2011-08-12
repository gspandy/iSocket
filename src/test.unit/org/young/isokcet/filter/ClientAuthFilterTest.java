/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.filter;

import java.io.IOException;

import junit.framework.Assert;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.TestFilterChainContextFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.young.isokcet.filter.ClientAuthFilter.ConnectionAuthInfo;
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
public class ClientAuthFilterTest {

    private static ClientAuthFilter cfilter;

    private static ParserFactory parserFactory;

    @BeforeClass
    public static void beforeClass() {
        cfilter = new ClientAuthFilter("isokcetuser", "password");
        parserFactory = new ParserFactory();
    }

    @AfterClass
    public static void afterClass() {
        cfilter = null;
        parserFactory = null;
    }

    /**
     * 
     * <p>
     * 描述:没有auth在内存中，没有认证通过
     * </p>
     * @param
     * @return
     * @throws
     * @see
     * @since %I%
     */
    @Test
    public void testHandleWriteNoAuth() {
        TestFilterChainContextFactory f = new TestFilterChainContextFactory();
        FilterChainContext ctx = f.getFilterChainContext();
        ctx.getInternalContext().setProcessor(new NullProcessor());
        ServiceRequest message = new ServiceRequest();
        ctx.setMessage(message);
        try {
            cfilter.handleWrite(ctx);
            Connection connection = ctx.getConnection();

            Assert.assertNotNull(cfilter.getConnectionAuthInfo(connection).getPendingMessages());

        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * 
     * <p>
     * 描述:有auth在内存中，没有认证通过
     * </p>
     * @param
     * @return
     * @throws
     * @see
     * @since %I%
     */
    @Test
    public void testHandleWriteHasAuth() {
        TestFilterChainContextFactory f = new TestFilterChainContextFactory();
        FilterChainContext ctx = f.getFilterChainContext();
        ctx.getInternalContext().setProcessor(new NullProcessor());
        ServiceRequest message = new ServiceRequest();
        ctx.setMessage(message);
        try {
            Connection connection = ctx.getConnection();
            ConnectionAuthInfo authInfo = new ConnectionAuthInfo();
            authInfo.setSessionId("1");
            authInfo.setPendingMessages(null);
            cfilter.puttConnectionAuthInfoIfAbsent(connection, authInfo);
            cfilter.handleWrite(ctx);
            Assert.assertEquals("1", ((ServiceRequest) ctx.getMessage()).getSessionId());

        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testHandleReadAuthServiceSuccess() {
        TestFilterChainContextFactory f = new TestFilterChainContextFactory();
        FilterChainContext ctx = f.getFilterChainContext();
        ctx.getInternalContext().setProcessor(new NullProcessor());
        ServiceResponse message = new ServiceResponse();
        message.setResponseCode(SocketKeys.RESPONSE_CODE_SUCCESS);
        message.setResponseObject("1");
        message.setServiceId(SocketKeys.SERVICE_ID_AUTH);
        ctx.setMessage(message);
        try {
            Connection connection = ctx.getConnection();
            ConnectionAuthInfo authInfo = new ConnectionAuthInfo();
            authInfo.setSessionId(null);
            //authInfo.setPendingMessages(null);
            cfilter.puttConnectionAuthInfoIfAbsent(connection, authInfo);
            cfilter.handleRead(ctx);
            Assert.assertEquals("1", authInfo.getSessionId());
            Assert.assertNull(authInfo.getPendingMessages());

        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testhandleReadAuthServiceFail() {
        TestFilterChainContextFactory f = new TestFilterChainContextFactory();
        FilterChainContext ctx = f.getFilterChainContext();
        ctx.getInternalContext().setProcessor(new NullProcessor());
        ServiceResponse message = new ServiceResponse();
        message.setResponseCode(SocketKeys.RESPONSE_CODE_SERVICEERROR);
        message.setResponseMessage("error");
        message.setServiceId(SocketKeys.SERVICE_ID_AUTH);
        ctx.setMessage(message);
        try {
            Connection connection = ctx.getConnection();
            ConnectionAuthInfo authInfo = new ConnectionAuthInfo();
            authInfo.setSessionId(null);
            //authInfo.setPendingMessages(null);
            cfilter.puttConnectionAuthInfoIfAbsent(connection, authInfo);
            cfilter.handleRead(ctx);
            Assert.assertEquals(SocketKeys.RESPONSE_CODE_SERVICEERROR, message.getResponseCode());

        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testHandleReadCommServiceAuthSuccess() {
        TestFilterChainContextFactory f = new TestFilterChainContextFactory();
        FilterChainContext ctx = f.getFilterChainContext();
        ctx.getInternalContext().setProcessor(new NullProcessor());
        ServiceResponse message = new ServiceResponse();
        message.setResponseCode(SocketKeys.RESPONSE_CODE_SUCCESS);
        message.setResponseObject("1");
        message.setSessionId("1");
        message.setServiceId(SocketKeys.SERVICE_ID_AUTH);
        ctx.setMessage(message);

        try {
            Connection connection = ctx.getConnection();
            ConnectionAuthInfo authInfo = new ConnectionAuthInfo();
            authInfo.setSessionId("1");
            authInfo.setPendingMessages(null);
            cfilter.puttConnectionAuthInfoIfAbsent(connection, authInfo);
            cfilter.handleRead(ctx);
            Assert.assertEquals(((ServiceResponse) ctx.getMessage()).getResponseCode(),
                    SocketKeys.RESPONSE_CODE_SUCCESS);

        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testHandleReadCommServiceAuthFail() {
        TestFilterChainContextFactory f = new TestFilterChainContextFactory();
        FilterChainContext ctx = f.getFilterChainContext();
        ctx.getInternalContext().setProcessor(new NullProcessor());
        ServiceResponse message = new ServiceResponse();
        message.setResponseCode(SocketKeys.RESPONSE_CODE_AUTHENTICATIONERROR);
        message.setResponseObject("1");
        message.setSessionId("2");
        message.setServiceId(SocketKeys.SERVICE_ID_AUTH);
        ctx.setMessage(message);

        try {
            Connection connection = ctx.getConnection();
            ConnectionAuthInfo authInfo = new ConnectionAuthInfo();
            authInfo.setSessionId("1");
            authInfo.setPendingMessages(null);
            cfilter.puttConnectionAuthInfoIfAbsent(connection, authInfo);
            cfilter.handleRead(ctx);
            Assert.assertEquals(((ServiceResponse) ctx.getMessage()).getResponseCode(),
                    SocketKeys.RESPONSE_CODE_AUTHENTICATIONERROR);

        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

}
