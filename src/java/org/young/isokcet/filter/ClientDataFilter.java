/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.filter;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.glassfish.grizzly.memory.MemoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.young.icore.util.Assert;
import org.young.isokcet.service.ServiceRequest;
import org.young.isokcet.service.ServiceResponse;
import org.young.isokcet.util.SocketKeys;
import org.young.isokcet.validatior.ObjectSizeValidator;
import org.young.isokcet.validatior.TransformTypeValidator;

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
public class ClientDataFilter extends BaseFilter {

    private static final Logger logger = LoggerFactory.getLogger(ClientDataFilter.class);

    private static ParserFactory parserFactory = new ParserFactory();

    /**
     * struct {
     * service id 10byte
     * transaction id  32 byte
     * tranform type 1 byte
     * body lenth 10 byte
     * content  previous length decided
     * }
     */

    public static final int WRITE_HEADER_SIZE = SocketKeys.SIZE_SERVICE_ID + SocketKeys.SIZE_TRANSACTION_ID
            + SocketKeys.SIZE_TRANSFORM_TYPE + SocketKeys.SIZE_CONTENT + SocketKeys.SIZE_SESSION_ID;// service id + trans id +transform type +body length  + content

    /**
     * struct{
     * transaction id  32 byte
     * tranform type 1 byte
     * result code 4 byte
     * body lenth 10 byte
     * content  previous length decided
     * }
     */
    public static final int READ_HEADER_SIZE = SocketKeys.SIZE_SERVICE_ID + SocketKeys.SIZE_TRANSACTION_ID
            + SocketKeys.SIZE_TRANSFORM_TYPE + SocketKeys.SIZE_RESPONSE_CODE + SocketKeys.SIZE_CONTENT
            + SocketKeys.SIZE_SESSION_ID;//trans id+transform type+result code+result length + content

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

        ITextProtocolParser parser = parserFactory.getParser(message.getTransformType());

        Assert.notNull(parser, "parser must not be null");

        String serializeStr = message.getRequestObject() == null ? "" : parser.to(message.getRequestObject());

        ObjectSizeValidator.validate(serializeStr);

        final int size = WRITE_HEADER_SIZE + serializeStr.length();

        // Retrieve the memory manager
        final MemoryManager memoryManager = ctx.getConnection().getTransport().getMemoryManager();

        // allocate the buffer of required size
        final Buffer output = memoryManager.allocate(size);

        // Allow Grizzly core to dispose the buffer, once it's written
        output.allowBufferDispose(true);

        //header: sid 10
        output.put(message.getServiceId().getBytes(SocketKeys.CHARSET_NAME_UTF8));

        //header: id 32
        output.put(message.getId().getBytes(SocketKeys.CHARSET_NAME_UTF8));

        //header: transform type 1
        output.put(("" + message.getTransformType()).getBytes(SocketKeys.CHARSET_NAME_UTF8));

        //header: result message length
        output.put(StringUtils.leftPad("" + serializeStr.length(), 10, "0").getBytes(SocketKeys.CHARSET_NAME_UTF8));

        String sessionId = StringUtils.leftPad(
                StringUtils.isEmpty(message.getSessionId()) ? "" : message.getSessionId(), SocketKeys.SIZE_SESSION_ID,
                " ");
        output.put(sessionId.getBytes(SocketKeys.CHARSET_NAME_UTF8));

        // Body
        if (!StringUtils.isEmpty(serializeStr)) {
            output.put(serializeStr.getBytes(SocketKeys.CHARSET_NAME_UTF8));
        }

        // Set the Buffer as a context message
        ctx.setMessage(output.flip());

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
        final Buffer sourceBuffer = ctx.getMessage();

        final int sourceBufferLength = sourceBuffer.remaining();

        // If source buffer doesn't contain header
        if (sourceBufferLength < READ_HEADER_SIZE) {
            // stop the filterchain processing and store sourceBuffer to be
            // used next time
            return ctx.getStopAction(sourceBuffer);
        }

        //check
        //transform type
        byte[] types = new byte[SocketKeys.SIZE_TRANSFORM_TYPE];
        types[0] = sourceBuffer.get(SocketKeys.SIZE_SERVICE_ID + SocketKeys.SIZE_TRANSACTION_ID);//SIZE_CONTENT 后取1byte

        // Construct a  message
        final ServiceResponse tmpMsg = new ServiceResponse();

        String transformType = new String(types, SocketKeys.CHARSET_NAME_UTF8);
        TransformTypeValidator.validate(transformType);

        // Get the body length
        byte[] lens = new byte[SocketKeys.SIZE_CONTENT];
        for (int i = 0; i < SocketKeys.SIZE_CONTENT; i++) {
            lens[i] = sourceBuffer.get(i + SocketKeys.SIZE_SERVICE_ID + SocketKeys.SIZE_TRANSACTION_ID
                    + SocketKeys.SIZE_TRANSFORM_TYPE + SocketKeys.SIZE_RESPONSE_CODE);
        }
        //final int bodyLength = sourceBuffer.getInt(READ_HEADER_SIZE - 10);
        final int resultLength = Integer.parseInt(new String(lens, SocketKeys.CHARSET_NAME_UTF8));

        // The complete message length
        final int completeMessageLength = READ_HEADER_SIZE + resultLength;

        // If the source message doesn't contain entire body
        if (sourceBufferLength < completeMessageLength) {
            // stop the filterchain processing and store sourceBuffer to be
            // used next time
            return ctx.getStopAction(sourceBuffer);
        }

        // Check if the source buffer has more than 1 complete  message
        // If yes - split up the first message and the remainder
        final Buffer remainder = sourceBufferLength > completeMessageLength ? sourceBuffer.split(completeMessageLength)
                : null;

        // Construct a  message
        final ServiceResponse message = new ServiceResponse();

        byte[] sids = new byte[SocketKeys.SIZE_SERVICE_ID];
        sourceBuffer.get(sids);
        message.setServiceId(new String(sids, SocketKeys.CHARSET_NAME_UTF8));

        //set id
        byte[] ids = new byte[SocketKeys.SIZE_TRANSACTION_ID];
        sourceBuffer.get(ids);

        message.setId(new String(ids, SocketKeys.CHARSET_NAME_UTF8));

        //set transfrom type
        types = new byte[SocketKeys.SIZE_TRANSFORM_TYPE];
        sourceBuffer.get(types);
        message.setTransformType(new String(types, SocketKeys.CHARSET_NAME_UTF8));

        // result code
        final byte[] rescodes = new byte[SocketKeys.SIZE_RESPONSE_CODE];
        sourceBuffer.get(rescodes);
        message.setResponseCode(Integer.parseInt(new String(rescodes, SocketKeys.CHARSET_NAME_UTF8)));

        //body length
        lens = new byte[SocketKeys.SIZE_CONTENT];
        sourceBuffer.get(lens);

        //session id
        byte[] ssids = new byte[SocketKeys.SIZE_SESSION_ID];
        sourceBuffer.get(ssids);
        String sessionId = new String(ssids, SocketKeys.CHARSET_NAME_UTF8).trim();
        message.setSessionId(StringUtils.isEmpty(sessionId) ? null : sessionId);

        // result body
        if (resultLength > 0) {
            final byte[] results = new byte[resultLength];
            sourceBuffer.get(results);

            String rstMsg = new String(results, SocketKeys.CHARSET_NAME_UTF8);

            //success
            if (message.getResponseCode() == 0) {
                ITextProtocolParser parser = parserFactory.getParser(message.getTransformType());

                Assert.notNull(parser, "parser must not be null");

                Object responseObject = parser.from(rstMsg);

                message.setResponseObject(responseObject);
            } else {//error
                //set response message
                message.setResponseMessage(rstMsg);
            }
        }

        ctx.setMessage(message);

        // We can try to dispose the buffer
        sourceBuffer.tryDispose();

        // Instruct FilterChain to store the remainder (if any) and continue execution
        return ctx.getInvokeAction(remainder);
    }
}
