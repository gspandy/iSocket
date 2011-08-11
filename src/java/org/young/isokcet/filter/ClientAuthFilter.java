/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.young.icore.util.PropertiesLoaderUtils;
import org.young.isokcet.exception.AuthenticationException;
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
public class ClientAuthFilter extends BaseFilter {

    private static final Logger logger = LoggerFactory.getLogger(ClientAuthFilter.class);

    // Map of authenticated connections
    private ConcurrentHashMap<Connection, ConnectionAuthInfo> authenticatedConnections = new ConcurrentHashMap<Connection, ConnectionAuthInfo>();

    private ServiceRequest authRequest;

    public ClientAuthFilter(String userName, String password) {
        PropertiesLoaderUtils.setPropertiesFields(this);
        authRequest = new ServiceRequest();
        //authRequest.setAuth(true);
        authRequest.setServiceId(SocketKeys.SERVICE_ID_AUTH);
        Map<String, String> user = new HashMap<String, String>();
        user.put(SocketKeys.PARAMETER_USERNAME, userName);
        user.put(SocketKeys.PARAMETER_PASSWORD, password);
        authRequest.setRequestObject(user);
    }

    @Override
    public NextAction handleRead(FilterChainContext ctx) throws IOException {
        // Get the connection
        final Connection connection = ctx.getConnection();
        // Get the processing packet
        final ServiceResponse serviceResponse = (ServiceResponse) ctx.getMessage();

        // Check if the packet is authentication response
        if (serviceResponse.getServiceId().equals(SocketKeys.SERVICE_ID_AUTH)) {
            // if yes - retrieve the id, assigned by server
            if (serviceResponse.getResponseCode() == SocketKeys.RESPONSE_CODE_SUCCESS) {
                final String sessionId = serviceResponse.getResponseObject();

                synchronized (connection) {
                    // store id in the map
                    ConnectionAuthInfo info = authenticatedConnections.get(connection);
                    if (info == null) {
                        serviceResponse.setResponseCode(SocketKeys.RESPONSE_CODE_AUTHENTICATIONERROR);
                        serviceResponse.setResponseMessage(SocketKeys.MESSAGE_AUTH_DELETED);
                    }

                    info.sessionId = sessionId;
                    info.setState(1);

                    // resume pending writes
                    for (FilterChainContext pendedContext : info.pendingMessages) {
                        pendedContext.resume();
                    }
                    info.pendingMessages = null;

                }

            } else {
                logger.error(String.format(SocketKeys.MESSAGE_SERVICE_ERROR, new Object[] { serviceResponse.getId(),
                        serviceResponse.getServiceId(), serviceResponse.getResponseMessage() }));

                synchronized (connection) {
                    // store id in the map
                    ConnectionAuthInfo info = authenticatedConnections.get(connection);
                    if (info == null) {
                        serviceResponse.setResponseCode(SocketKeys.RESPONSE_CODE_AUTHENTICATIONERROR);
                        serviceResponse.setResponseMessage(SocketKeys.MESSAGE_AUTH_DELETED);
                    }

                    info.sessionId = null;
                    info.setState(2);

                    // resume pending writes
                    for (FilterChainContext pendedContext : info.pendingMessages) {
                        pendedContext.resume();
                    }

                    info.pendingMessages = null;

                }

            }

            // if it's authentication response - we don't pass processing to a next filter in a chain.
            return ctx.getStopAction();
        } else {
            // if it's some custom message
            // Get id line
            final String sessionId = serviceResponse.getSessionId();

            // Check the client id
            if (checkAuth(connection, sessionId)) {
                // if id corresponds to what client has -
                // Remove authentication header
                serviceResponse.setSessionId(null);//remove sessionId

                // Pass to a next filter
                return ctx.getInvokeAction();
            } else {

                serviceResponse.setResponseCode(SocketKeys.RESPONSE_CODE_AUTHENTICATIONERROR);
                serviceResponse.setResponseMessage(SocketKeys.MESSAGE_AUTH_ERROR);

                return ctx.getInvokeAction();
            }
        }
    }

    @Override
    public NextAction handleClose(FilterChainContext ctx) throws IOException {
        authenticatedConnections.remove(ctx.getConnection());

        return ctx.getInvokeAction();
    }

    public ConnectionAuthInfo getConnectionAuthInfo(Connection connection) {
        return authenticatedConnections.get(connection);
    }

    public void puttConnectionAuthInfoIfAbsent(Connection connection, ConnectionAuthInfo authInfo) {
        authenticatedConnections.putIfAbsent(connection, authInfo);
    }

    @Override
    public NextAction handleWrite(final FilterChainContext ctx) throws IOException {

        // Get the connection
        final Connection connection = ctx.getConnection();

        //get send buffer
        final ServiceRequest sourceRequst = ctx.getMessage();

        // Get the connection authentication information
        ConnectionAuthInfo authInfo = authenticatedConnections.get(connection);

        if (authInfo == null) {
            // connection is not authenticated
            authInfo = new ConnectionAuthInfo();
            final ConnectionAuthInfo existingInfo = authenticatedConnections.putIfAbsent(connection, authInfo);
            if (existingInfo == null) {
                // it's the first message for this client - we need to start authentication process
                // sending authentication packet
                authRequest.setTransformType(sourceRequst.getTransformType());
                ctx.write(authRequest);
            } else {
                // authentication has been already started.
                authInfo = existingInfo;
            }
        }

        //if (sourceRequst.getServiceId().equals(SocketKeys.SERVICE_ID_AUTH)) {

        if (authInfo.getState() == 0) {//un authenticate

            //if (authInfo.pendingMessages != null) {
            // it might be a sign, that authentication has been completed on another thread
            // synchronize and check one more time
            synchronized (connection) {
                //if (authInfo.pendingMessages != null) {
                if ((authInfo.getState() == 0)) {
                    if (authInfo.sessionId == null) {
                        // Authentication hs been started by another thread, but it is still in progress
                        // add suspended write context to a queue
                        ctx.suspend();
                        authInfo.pendingMessages.add(ctx);
                        return ctx.getSuspendAction();

                    }
                }
            }

        } else if (authInfo.getState() == 1) {//authenticate success

        } else if (authInfo.getState() == 2) {//authenticate failure
            if (sourceRequst.getServiceId().equals(SocketKeys.SERVICE_ID_AUTH)) {

            } else {
                throw new AuthenticationException(SocketKeys.MESSAGE_AUTH_ERROR);
            }

        } else {
            throw new IllegalArgumentException(String.format("auth info state:%s is error.", authInfo.getState()));
        }

        // Authentication has been completed - add "auth-id" header and pass the message to a next filter in chain.
        sourceRequst.setSessionId(authInfo.getSessionId());
        return ctx.getInvokeAction();
    }

    private NextAction handleAuthFail(FilterChainContext ctx, ServiceRequest sourceRequest, String errMsg,
            Object... objects) throws IOException {
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setResponseCode(SocketKeys.RESPONSE_CODE_AUTHENTICATIONERROR);
        serviceResponse.setResponseMessage(String.format(errMsg, objects));
        serviceResponse.setTransformType(sourceRequest.getTransformType());
        //serviceResponse.setAuth(sourceRequest.isAuth());
        serviceResponse.setId(sourceRequest.getId());
        serviceResponse.setSessionId(sourceRequest.getSessionId());
        serviceResponse.setServiceId(sourceRequest.getServiceId());
        ctx.write(serviceResponse);
        // stop the packet processing
        return ctx.getStopAction();
    }

    private boolean checkAuth(Connection connection, String sessionId) {
        // Get the connection id, from the client map
        final ConnectionAuthInfo registeredId = authenticatedConnections.get(connection);
        if (registeredId == null || registeredId.sessionId == null)
            return false;

        return sessionId.equals(registeredId.sessionId);

    }

    /**
     * Single connection authentication info.
     */
    public static class ConnectionAuthInfo {
        // Connection id
        private volatile String sessionId;

        // Queue of the pending writes
        private volatile Queue<FilterChainContext> pendingMessages;

        private int state = 0;

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public Queue<FilterChainContext> getPendingMessages() {
            return pendingMessages;
        }

        public void setPendingMessages(Queue<FilterChainContext> pendingMessages) {
            this.pendingMessages = pendingMessages;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public ConnectionAuthInfo() {
            pendingMessages = new ConcurrentLinkedQueue<FilterChainContext>();
        }

        public String getSessionId() {
            return this.sessionId;
        }

    }

}
