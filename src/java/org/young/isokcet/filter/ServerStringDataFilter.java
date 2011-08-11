/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.filter;

import java.io.IOException;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.young.icore.util.Assert;
import org.young.isokcet.service.ServiceRequest;
import org.young.isokcet.service.ServiceResponse;
import org.young.isokcet.util.SocketKeys;
import org.young.isokcet.validatior.TransactionIdValidator;

/**
 * <p>
 * 描述:数据封装Filter,与ServerDataFilter区别在于报文头的定义，全部用字符串解析，便于接入其他语言。
 * </p>
 *
 * @see
 * @author yangjun2
 * @email yangjun1120@gmail.com
 *
 */
public class ServerStringDataFilter extends BaseFilter {

    private static final Logger logger = LoggerFactory.getLogger(ServerStringDataFilter.class);

    private static ParserFactory parserFactory = new ParserFactory();

    /**
     * Method is called, when we write a data to the Connection.
     *
     * We override this method to perform iSocket Message -> Buffer transformation.
     *
     * @param ctx Context of {@link FilterChainContext} processing
     * @return the next action
     * @throws java.io.IOException
     */
    @Override
    public NextAction handleWrite(final FilterChainContext ctx) throws IOException {
        final ServiceResponse message = ctx.getMessage();
        Assert.notNull(message, "service response must not be null!");

        TransactionIdValidator.validate(message.getId());

        ITextProtocolParser parser = parserFactory.getParser(SocketKeys.TRANSFORM_JSON);

        String serializeStr = parser.to(message);

        logger.debug("write tid:{},sid:{},message:{}", new Object[] { message.getId(), message.getServiceId(),
                serializeStr });

        // Set the Buffer as a context message
        ctx.setMessage(serializeStr);

        // Instruct the FilterChain to call the next filter
        return ctx.getInvokeAction();
    }

    /**
     * Method is called, when new data was read from the Connection and ready
     * to be processed.
     *
     * We override this method to perform Buffer -> ISocket Message transformation.
     * 
     * @param ctx Context of {@link FilterChainContext} processing
     * @return the next action
     * @throws java.io.IOException
     */
    @Override
    public NextAction handleRead(final FilterChainContext ctx) throws IOException {
        Connection conn = null;
        try {
            // Get the source buffer from the context
            final String message = ctx.getMessage();

            ITextProtocolParser parser = parserFactory.getParser(SocketKeys.TRANSFORM_JSON);

            ServiceRequest request = parser.from(message);

            logger.debug("read tid:{},sid:{},message:{}", new Object[] { request.getId(), request.getServiceId(),
                    message });

            ctx.setMessage(request);

            // Instruct FilterChain to store the remainder (if any) and continue execution
            return ctx.getInvokeAction();
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

}
