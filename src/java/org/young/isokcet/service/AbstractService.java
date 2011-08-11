/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.service;

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
abstract public class AbstractService implements ISocketService {

    private String serviceId;

    public String getServiceId() {
        return serviceId;
    }

    void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    /* (non-Javadoc)
     * @see org.young.isokcet.service.ISocketService#invoke(org.young.isokcet.model.ServiceRequest)
     */
    @Override
    final public Object invoke(ServiceRequest svcReq) {
        doInvokeBefore(svcReq);
        Object responseObject = doInvoke(svcReq);
        doInvokeAfter(svcReq, responseObject);
        return responseObject;
    }

    abstract public void doInvokeBefore(ServiceRequest svcReq);

    abstract public void doInvokeAfter(ServiceRequest svcReq, Object obj);

    abstract public Object doInvoke(ServiceRequest svcReq);

}
