/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.service;

import org.young.isokcet.util.SocketKeys;

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

    private static ServiceFactory sf;

    public ServiceController() {
        sf = ServiceFactory.getInstance();
    }

    public ServiceResponse invoke(ServiceRequest svcReq) {
        ServiceResponse svcRes = null;
        try {
            String serviceId = svcReq.getServiceId();
            ISocketService service = sf.getService(serviceId);
            Object obj = service.invoke(svcReq);
            svcRes = handleSuccess(svcReq, obj);

        } catch (Exception e) {
            svcRes = handleThrowable(svcReq, e, SocketKeys.RESPONSE_CODE_SERVICEERROR);
        } catch (Error e) {
            svcRes = handleThrowable(svcReq, e, SocketKeys.RESPONSE_CODE_SERVERERROR);
        } finally {
            //svcRes.setAuth(svcReq.isAuth());
        }

        return svcRes;
    }

    private ServiceResponse handleThrowable(ServiceRequest req, Throwable e, int errorCode) {
        ServiceResponse svcRes = new ServiceResponse();
        svcRes.setId(req.getId());
        svcRes.setResponseCode(errorCode);
        svcRes.setResponseMessage(e.getMessage());
        //svcRes.setAuth(req.isAuth());
        svcRes.setTransformType(req.getTransformType());
        svcRes.setServiceId(req.getServiceId());
        return svcRes;
    }

    private ServiceResponse handleSuccess(ServiceRequest req, Object resObj) {
        ServiceResponse svcRes = new ServiceResponse();
        svcRes.setId(req.getId());
        svcRes.setResponseCode(SocketKeys.RESPONSE_CODE_SUCCESS);
        svcRes.setResponseObject(resObj);

        //svcRes.setAuth(req.isAuth());
        svcRes.setTransformType(req.getTransformType());
        svcRes.setServiceId(req.getServiceId());

        return svcRes;
    }

}
