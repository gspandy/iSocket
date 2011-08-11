/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.filter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ServerAuthFilter extends BaseFilter {
    private static final Logger logger = LoggerFactory.getLogger(ServerAuthFilter.class);

    // Authenticated clients connection map
    private Map<Connection, String> authenticatedConnections = new ConcurrentHashMap<Connection, String>();

    // Random, to generate client ids.
    //    private final Random random = new Random();

    /**
     * The method is called once we have received {@link MultiLinePacket} from
     * a client.
     * Filter check if incoming message is the client authentication request.
     * If yes - we generate new client id and send it back in the
     * authentication response. If the message is not authentication request -
     * we check message authentication header to correspond to a connection id
     * in the authenticated clients map. If it's ok - the filter removes
     * authentication header from the message and pass the message to a next
     * filter in a filter chain, otherwise, if authentication failed - the filter
     * throws an Exception
     * 
     * @param ctx Request processing context
     *
     * @return {@link NextAction}
     * @throws IOException
     */
    @Override
    public NextAction handleRead(FilterChainContext ctx) throws IOException {
        // Get the connection
        final Connection connection = ctx.getConnection();
        // Get the incoming packet
        final ServiceRequest sourceRequest = (ServiceRequest) ctx.getMessage();

        // get the command string
        // check if it's authentication request from a client
        if (sourceRequest.getServiceId().equals(SocketKeys.SERVICE_ID_AUTH)) {
            //check Service Id
            //            if (!sourceRequest.getServiceId().equals(SocketKeys.SERVICE_ID_AUTH)) {
            //                return handleAuthFail(ctx, sourceRequest, SocketKeys.MESSAGE_SERVICE_ERROR,
            //                        sourceRequest.getServiceId());
            //            }

            return ctx.getInvokeAction();
        } else {
            // if it's some custom message
            // Get id line
            final String sessionId = sourceRequest.getSessionId();

            // Check the client id
            if (checkAuth(connection, sessionId)) {
                // if id corresponds to what server has -
                // Remove authentication header
                sourceRequest.setSessionId(null);

                // Pass to a next filter
                return ctx.getInvokeAction();
            } else {
                return handleAuthFail(ctx, sourceRequest, SocketKeys.MESSAGE_AUTH_ERROR);
            }
        }
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

    /**
     * The method is called each time, when server sends a message to a client.
     * First of all filter check if this packet is not authentication-response.
     * If yes - filter just passes control to a next filter in a chain, if not -
     * filter gets the client id from its local authenticated clients map and
     * adds "auth-id: <connection-id>" header to the outgoing message and
     * finally passes control to a next filter in a chain.
     *
     * @param ctx Response processing context
     *
     * @return {@link NextAction}
     * @throws IOException
     */
    @Override
    public NextAction handleWrite(FilterChainContext ctx) throws IOException {

        // Get the connection
        final Connection connection = ctx.getConnection();
        // Get the sending packet
        final ServiceResponse sourceResponse = (ServiceResponse) ctx.getMessage();

        // if it's authentication-response
        if (sourceResponse.getServiceId().equals(SocketKeys.SERVICE_ID_AUTH)) {
            // just pass control to a next filter in a chain
            if (sourceResponse.getResponseCode() == SocketKeys.RESPONSE_CODE_SUCCESS) {
                String sessionId = sourceResponse.getResponseObject();
                authenticatedConnections.put(connection, sessionId);
                sourceResponse.setSessionId(sessionId);
            } else {
                //modify error code
                sourceResponse.setResponseCode(SocketKeys.RESPONSE_CODE_AUTHENTICATIONERROR);
            }
            return ctx.getInvokeAction();
        } else {
            // if not - get connection id from authenticated connections map
            final String sessionId = authenticatedConnections.get(connection);
            if (sessionId != null) {
                // if id exists - add "auth-id" header to a packet
                sourceResponse.setSessionId(sessionId);
                // pass control to a next filter in a chain
                //                return ctx.getInvokeAction();
            } else {
                sourceResponse.setResponseCode(SocketKeys.RESPONSE_CODE_AUTHENTICATIONERROR);
                sourceResponse.setResponseMessage(String.format(SocketKeys.MESSAGE_AUTH_ERROR, new Object[] {
                        sourceResponse.getId(), sourceResponse.getServiceId(), sourceResponse.getResponseMessage() }));
            }
            return ctx.getInvokeAction();

        }
    }

    /**
     * Method checks, whether authentication header, sent in the message corresponds
     * to a value, stored in the server authentication map.
     * 
     * @param connection {@link Connection}
     * @param idLine authentication header string.
     * 
     * @return <tt>true</tt>, if authentication passed, or <tt>false</tt> otherwise.
     */
    private boolean checkAuth(Connection connection, String sessionId) {
        if (sessionId == null)
            return false;
        // Get the connection id, from the server map
        final String registeredId = authenticatedConnections.get(connection);
        if (registeredId == null)
            return false;

        return sessionId.equals(registeredId);
    }

    /**
     * The method is called, when a connection gets closed.
     * We remove connection entry in authenticated connections map.
     *
     * @param ctx Request processing context
     *
     * @return {@link NextAction}
     * @throws IOException
     */
    @Override
    public NextAction handleClose(FilterChainContext ctx) throws IOException {
        authenticatedConnections.remove(ctx.getConnection());
        return ctx.getInvokeAction();
    }

}
