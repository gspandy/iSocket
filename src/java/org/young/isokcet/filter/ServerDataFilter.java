/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.filter;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.glassfish.grizzly.memory.MemoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.young.icore.util.Assert;
import org.young.isokcet.service.ServiceFactory;
import org.young.isokcet.service.ServiceRequest;
import org.young.isokcet.service.ServiceResponse;
import org.young.isokcet.util.SocketKeys;
import org.young.isokcet.validatior.ObjectSizeValidator;
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
public class ServerDataFilter extends BaseFilter {

    private static final Logger logger = LoggerFactory.getLogger(ServerDataFilter.class);

    private static ParserFactory parserFactory = new ParserFactory();

    //    private static Map<Connection, Long> remainderTimeMap = new HashMap<Connection, Long>();

    /**
     * struct {
     *  service id 10byte
     *  transaction id  32 byte
     *  tranform type 1 byte
     *  body lenth 10 byte
     *  content  previous length decided
     * }
     */
    public static final int READ_HEADER_SIZE = ClientDataFilter.WRITE_HEADER_SIZE;

    /**
     * struct{
     *  transaction id  32 byte
     *  tranform type 1 byte
     *  result code 4 byte
     *  body lenth 10 byte
     *  content  previous length decided
     * }
     */
    public static final int WRITE_HEADER_SIZE = ClientDataFilter.READ_HEADER_SIZE;

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

        int size = WRITE_HEADER_SIZE;
        int resSize = 0;
        String serializeStr = null;
        if (message.getResponseCode() == 0) {
            ITextProtocolParser parser = parserFactory.getParser(message.getTransformType());
            Assert.notNull(parser, "parser must not be null");

            serializeStr = parser.to(message.getResponseObject());
            resSize = (serializeStr != null) ? serializeStr.length() : 0;
        } else {
            serializeStr = message.getResponseMessage();
            resSize = (message.getResponseMessage() != null ? message.getResponseMessage().length() : 0);
        }

        ObjectSizeValidator.validate(serializeStr);

        size += resSize;

        // Retrieve the memory manager
        final MemoryManager memoryManager = ctx.getConnection().getTransport().getMemoryManager();

        // allocate the buffer of required size
        final Buffer output = memoryManager.allocate(size);

        // Allow Grizzly core to dispose the buffer, once it's written
        output.allowBufferDispose(true);

        //header:service id
        output.put(message.getServiceId().getBytes(SocketKeys.CHARSET_NAME_UTF8));

        //header:transaction id
        output.put(message.getId().getBytes(SocketKeys.CHARSET_NAME_UTF8));

        //header:transform type
        output.put(("" + message.getTransformType()).getBytes(SocketKeys.CHARSET_NAME_UTF8));

        //header: result code
        output.put(StringUtils.leftPad("" + message.getResponseCode(), SocketKeys.SIZE_RESPONSE_CODE, "0").getBytes(
                SocketKeys.CHARSET_NAME_UTF8));

        //header: result message length
        output.put(StringUtils.leftPad("" + resSize, SocketKeys.SIZE_CONTENT, "0").getBytes(
                SocketKeys.CHARSET_NAME_UTF8));

        //header: session id;
        String sessionId = StringUtils.leftPad(
                StringUtils.isEmpty(message.getSessionId()) ? "" : message.getSessionId(), SocketKeys.SIZE_SESSION_ID,
                " ");
        output.put(sessionId.getBytes(SocketKeys.CHARSET_NAME_UTF8));

        // result message
        //serializeStr = (serializeStr == null) ? "" : serializeStr;
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
        Connection conn = null;
        try {
            // Get the source buffer from the context
            final Buffer sourceBuffer = ctx.getMessage();
            conn = ctx.getConnection();

            final int sourceBufferLength = sourceBuffer.remaining();

            // If source buffer doesn't contain header
            if (sourceBufferLength < READ_HEADER_SIZE) {
                // stop the filterchain processing and store sourceBuffer to be
                // used next time
                return ctx.getStopAction(sourceBuffer);
            }

            //检查，并没有移动Buffer的pos
            //check service id
            byte[] svcs = new byte[SocketKeys.SIZE_SERVICE_ID];
            for (int i = 0; i < SocketKeys.SIZE_SERVICE_ID; i++) {
                svcs[i] = sourceBuffer.get(i);
            }
            String serviceId = new String(svcs, SocketKeys.CHARSET_NAME_UTF8);
            Assert.isTrue(ServiceFactory.getInstance().isExist(serviceId),
                    String.format("service id:%s isn't exists!", serviceId));

            //check transform type
            byte[] types = new byte[SocketKeys.SIZE_TRANSFORM_TYPE];
            types[0] = sourceBuffer.get(SocketKeys.SIZE_SERVICE_ID + SocketKeys.SIZE_TRANSACTION_ID);
            String transformType = new String(types, SocketKeys.CHARSET_NAME_UTF8);

            Assert.isTrue(
                    transformType.equals(SocketKeys.TRANSFORM_JSON) || transformType.equals(SocketKeys.TRANSFORM_XML),
                    String.format("tranform type(%s) is valid", transformType));

            // Get the body length
            byte[] lens = new byte[SocketKeys.SIZE_CONTENT];
            for (int i = 0; i < SocketKeys.SIZE_CONTENT; i++) {
                lens[i] = sourceBuffer.get(i + SocketKeys.SIZE_SERVICE_ID + SocketKeys.SIZE_TRANSACTION_ID
                        + SocketKeys.SIZE_TRANSFORM_TYPE);
            }
            final int bodyLength = Integer.parseInt(new String(lens, SocketKeys.CHARSET_NAME_UTF8));

            // The complete message length
            final int completeMessageLength = READ_HEADER_SIZE + bodyLength;

            // If the source message doesn't contain entire body
            if (sourceBufferLength < completeMessageLength) {
                // stop the filterchain processing and store sourceBuffer to be
                // used next time
                return ctx.getStopAction(sourceBuffer);
            }

            //检查结束

            //转换
            //transform to ServiceRequest
            final ServiceRequest message = new ServiceRequest();

            //set service id 10
            svcs = new byte[SocketKeys.SIZE_SERVICE_ID];
            sourceBuffer.get(svcs);
            message.setServiceId(new String(svcs, SocketKeys.CHARSET_NAME_UTF8));

            //set id  32
            final byte[] ids = new byte[SocketKeys.SIZE_TRANSACTION_ID];
            sourceBuffer.get(ids);
            message.setId(new String(ids, SocketKeys.CHARSET_NAME_UTF8));

            //set transform type 1
            types = new byte[SocketKeys.SIZE_TRANSFORM_TYPE];
            sourceBuffer.get(types);
            message.setTransformType(new String(types, SocketKeys.CHARSET_NAME_UTF8));

            // Get the body length
            lens = new byte[SocketKeys.SIZE_CONTENT];
            sourceBuffer.get(lens);

            final byte[] ssids = new byte[SocketKeys.SIZE_SESSION_ID];
            sourceBuffer.get(ssids);
            String sessionId = new String(ssids, SocketKeys.CHARSET_NAME_UTF8).trim();
            message.setSessionId(StringUtils.isEmpty(sessionId) ? null : sessionId);

            // Check if the source buffer has more than 1 complete  message
            // If yes - split up the first message and the remainder
            final Buffer remainder = sourceBufferLength > completeMessageLength ? sourceBuffer
                    .split(completeMessageLength) : null;

            // result body
            if (bodyLength > 0) {
                final byte[] requests = new byte[bodyLength];
                sourceBuffer.get(requests);

                String requestStr = new String(requests, SocketKeys.CHARSET_NAME_UTF8);

                ITextProtocolParser parser = parserFactory.getParser(message.getTransformType());

                Assert.notNull(parser, "parser must not be null");

                Object requestObject = parser.from(requestStr);

                //set result
                message.setRequestObject(requestObject);
            }

            ctx.setMessage(message);

            // We can try to dispose the buffer
            sourceBuffer.tryDispose();

            //转换结束

            // Instruct FilterChain to store the remainder (if any) and continue execution
            return ctx.getInvokeAction(remainder);
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
