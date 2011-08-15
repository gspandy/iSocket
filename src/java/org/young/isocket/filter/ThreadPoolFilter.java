/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isocket.filter;

import java.io.IOException;

import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.young.isocket.service.ServiceController;
import org.young.isocket.service.ServiceRequest;
import org.young.isocket.service.ServiceResponse;
import org.young.isocket.threadpool.Job;
import org.young.isocket.threadpool.JobDispatcher;
import org.young.isocket.util.SocketKeys;

/**
 * <p>
 * 描述:子定义线程池Filter
 * </p>
 * 
 * @see
 * @author yangjun2
 * @email yangjun1120@gmail.com
 * 
 */
public class ThreadPoolFilter extends BaseFilter {

	private static final Logger logger = LoggerFactory
			.getLogger(ThreadPoolFilter.class);

	private JobDispatcher jobDispatcher;

	private static ServiceController serviceCtrl;

	public ThreadPoolFilter(JobDispatcher jobDispatcher) {
		this.jobDispatcher = jobDispatcher;
		serviceCtrl = new ServiceController();
	}

	/**
	 * Handle just read operation, when some message has come and ready to be
	 * processed.
	 * 
	 * @param ctx
	 *            Context of {@link FilterChainContext} processing
	 * @return the next action
	 * @throws java.io.IOException
	 */
	@Override
	public NextAction handleRead(FilterChainContext ctx) throws IOException {

		final FilterChainContext ctxTmp = ctx;
		// Peer address is used for non-connected UDP Connection :)
		final Object peerAddress = ctxTmp.getAddress();

		final ServiceRequest message = ctxTmp.getMessage();

		if (logger.isDebugEnabled())
			logger.debug("address:{},message:{}", new Object[] { peerAddress,
					message });

		if (message != null) {// import:message is not null!

			// Get the SuspendAction in advance, cause once we execute
			// LongLastTask in the
			// custom thread - we lose control over the context
			final NextAction suspendAction = ctxTmp.getSuspendAction();

			// suspend the current execution
			ctxTmp.suspend();

			jobDispatcher.execute(new Job() {

				public String getKey() {
					return message.getServiceId();
				}

				public void run() {
					ServiceResponse response = null;
					try {
						response = serviceCtrl.invoke(message);

					} catch (Exception e) {
						if (serviceCtrl.isCommonService(message)) {
						response = new ServiceResponse();
						response.setResponseCode(SocketKeys.RESPONSE_CODE_UNKNOWERROR);
						response.setResponseMessage(new StringBuffer().append(
								e.getMessage()).toString());
						} else {
							logger.error(
									String.format(
											"notify service execute error,service id:%s ,reqid:%s,request:%s",
											message.getServiceId(), message.getId(),
											""+message.getRequestObject()), e);
						}
					} 

					try {
						if (serviceCtrl.isCommonService(message)) { // common service write back to client only.
							response.setTransformType(message.getTransformType());
							// set the message null, to let our filter to
							// distinguish resumed context
							ctxTmp.setMessage(response);
							// write client address
							ctxTmp.write(peerAddress, response, null);
						}

						// set the message null, to let our filter to
						// distinguish resumed context
						ctxTmp.setMessage(null);

						// resume the context
						ctxTmp.resume();
					} catch (Exception e) {
						logger.error("Job Thread execute error.", e);
					}

				}

			});

			// return suspend status
			return suspendAction;

		} else {// resume message is null

			return ctx.getStopAction();
		}
	}
}
