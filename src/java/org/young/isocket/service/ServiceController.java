/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isocket.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.young.isocket.filter.ThreadPoolFilter;
import org.young.isocket.util.SocketKeys;

/**
 * <p>
 * 服务Controller类
 * </p>
 * 
 * @see
 * @author yangjun2
 * @email yangjun1120@gmail.com
 * 
 */
public class ServiceController {

	private static final Logger logger = LoggerFactory
			.getLogger(ServiceController.class);

	private static ServiceFactory sf;

	public ServiceController() {
		sf = ServiceFactory.getInstance();
	}

	public boolean isNotifyService(ServiceRequest svcReq) {
		String serviceId = svcReq.getServiceId();
		IService service = sf.getService(serviceId);

		return (service instanceof INotifyService);
	}

	public boolean isCommonService(ServiceRequest svcReq) {
		String serviceId = svcReq.getServiceId();
		IService service = sf.getService(serviceId);
		return (service instanceof ICommonService);
	}

	public ServiceResponse invoke(ServiceRequest svcReq) {
		ServiceResponse svcRes = null;
		//try {
			String serviceId = svcReq.getServiceId();
			IService service = sf.getService(serviceId);
			if (service instanceof INotifyService) {
				doInvokeNotifyService(((INotifyService) service),svcReq);
			} else if (service instanceof ICommonService) {
				svcRes = doInvokeCommonService(((ICommonService) service),svcReq);
			}

		// } catch (Exception e) {
		// svcRes = handleThrowable(svcReq, e,
		// SocketKeys.RESPONSE_CODE_SERVICEERROR);
		// } catch (Error e) {
		// svcRes = handleThrowable(svcReq, e,
		// SocketKeys.RESPONSE_CODE_SERVERERROR);
		// } finally {
		// // svcRes.setAuth(svcReq.isAuth());
		// }

		return svcRes;
	}

	public void doInvokeNotifyService(INotifyService s, ServiceRequest svcReq) {
		try {
			s.invoke(svcReq);

		} catch (Exception e) {
			logger.error(
					String.format(
							"notify service execute error,service id:%s ,reqid:%s,request:%s",
							svcReq.getServiceId(), svcReq.getId(),
							""+svcReq.getRequestObject()), e);

		} catch (Error e) {
			logger.error(
					String.format(
							"notify service execute error,service id:%s ,reqid:%s,request:%s",
							svcReq.getServiceId(), svcReq.getId(),
							""+svcReq.getRequestObject()), e);

		}
	}

	public ServiceResponse doInvokeCommonService(ICommonService s,
			ServiceRequest svcReq) {
		ServiceResponse svcRes = null;
		try {

			Object obj = s.invoke(svcReq);
			svcRes = handleSuccess(svcReq, obj);

		} catch (Exception e) {
			svcRes = handleThrowable(svcReq, e,
					SocketKeys.RESPONSE_CODE_SERVICEERROR);
		} catch (Error e) {
			svcRes = handleThrowable(svcReq, e,
					SocketKeys.RESPONSE_CODE_SERVERERROR);
		}

		return svcRes;
	}

	private ServiceResponse handleThrowable(ServiceRequest req, Throwable e,
			int errorCode) {
		ServiceResponse svcRes = new ServiceResponse();
		svcRes.setId(req.getId());
		svcRes.setResponseCode(errorCode);
		svcRes.setResponseMessage(e.getMessage());
		// svcRes.setAuth(req.isAuth());
		svcRes.setTransformType(req.getTransformType());
		svcRes.setServiceId(req.getServiceId());
		return svcRes;
	}

	private ServiceResponse handleSuccess(ServiceRequest req, Object resObj) {
		ServiceResponse svcRes = new ServiceResponse();
		svcRes.setId(req.getId());
		svcRes.setResponseCode(SocketKeys.RESPONSE_CODE_SUCCESS);
		svcRes.setResponseObject(resObj);

		// svcRes.setAuth(req.isAuth());
		svcRes.setTransformType(req.getTransformType());
		svcRes.setServiceId(req.getServiceId());

		return svcRes;
	}

}
