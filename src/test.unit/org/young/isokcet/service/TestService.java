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
public class TestService extends AbstractService {

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
        System.out.println(svcReq.toString());
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        return svcReq.getRequestObject();
    }

    public static void main(String[] args) {
        TestService t = new TestService();
        t.setServiceId("1");
    }

}
