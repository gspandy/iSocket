/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.filter;

import java.io.IOException;

import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.young.icore.util.Assert;
import org.young.isokcet.service.ServiceRequest;
import org.young.isokcet.service.ServiceResponse;
import org.young.isokcet.util.SocketKeys;
import org.young.isokcet.validatior.ObjectSizeValidator;

/**
 * <p>
 * 描述:数据封装Filter
 * </p>
 *
 * @see
 * @author yangjun2
 * @email yangjun1120@gmail.com
 *
 */
public class ClientStringDataFilter extends BaseFilter {

    private static final Logger logger = LoggerFactory.getLogger(ClientStringDataFilter.class);

    private static ParserFactory paserFactory = new ParserFactory();

    /**
     * Method is called, when we write a data to the Connection.
     *
     * We override this method to perform iSocket Message -> Buffer transformation.
     *sslFilter
     * @param ctx Context of {@link FilterChainContext} processing
     * @return the next action
     * @throws java.io.IOException
     */
    @Override
    public NextAction handleWrite(final FilterChainContext ctx) throws IOException {

        if (logger.isDebugEnabled()) {
            logger.debug("context:{} handleWrite.", ctx);

        }
        ServiceRequest message = ctx.getMessage();

        Assert.notNull(message, "service request must not be null!");

        Assert.isTrue((message.getId() != null) && (message.getId().length() == 32), "length of Id must be 32 byte");

        Assert.isTrue((message.getServiceId() != null) && (message.getServiceId().length() == 10),
                "length of ServiceId must be 10 byte");

        ITextProtocolParser parser = paserFactory.getParser(SocketKeys.TRANSFORM_JSON);

        String serializeStr = parser.to(message);

        ObjectSizeValidator.validate(serializeStr);

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

        if (logger.isDebugEnabled()) {
            logger.debug("context:{} handleRead.", ctx);

        }

        // Get the source buffer from the context
        final String message = ctx.getMessage();

        ITextProtocolParser parser = paserFactory.getParser(SocketKeys.TRANSFORM_JSON);

        ServiceResponse servieResponse = parser.from(message);

        ctx.setMessage(servieResponse);

        // Instruct FilterChain to store the remainder (if any) and continue execution
        return ctx.getInvokeAction();
    }
}
