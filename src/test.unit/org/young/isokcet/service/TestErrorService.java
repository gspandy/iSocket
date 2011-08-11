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
public class TestErrorService extends AbstractService {

    /* (non-Javadoc)
     * @see org.young.isokcet.service.AbstractService#doInvokeBefore(org.young.isokcet.model.ServiceRequest)
     */
    @Override
    public void doInvokeBefore(ServiceRequest svcReq) {

    }

    /* (non-Javadoc)
     * @see org.young.isokcet.service.AbstractService#doInvokeAfter(org.young.isokcet.model.ServiceRequest, org.young.isokcet.model.ServiceResponse)
     */
    @Override
    public void doInvokeAfter(ServiceRequest svcReq, Object obj) {

    }

    /* (non-Javadoc)
     * @see org.young.isokcet.service.AbstractService#doInvoke(org.young.isokcet.model.ServiceRequest)
     */
    @Override
    public Object doInvoke(ServiceRequest svcReq) {
        if (true)
            throw new RuntimeException("test error!");

        return null;
    }

    public static void main(String[] args) {
        TestErrorService t = new TestErrorService();
        t.setServiceId("1");
    }

}
