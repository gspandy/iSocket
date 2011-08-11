/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.service;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.young.isokcet.util.SocketKeys;

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
public class ServiceControllerTest {
    private static ServiceController controller;

    @BeforeClass
    public static void beforeClass() {
        controller = new ServiceController();
    }

    @AfterClass
    public static void afterClass() {
        controller = null;
    }

    @Test
    public void testInvokeSucess() {
        ServiceRequest message1 = new ServiceRequest();
        message1.setId("11111111111111111111111111111122");
        message1.setRequestObject("test string");
        message1.setServiceId("1000010001");
        message1.setTransformType(SocketKeys.TRANSFORM_JSON);
        ServiceResponse response = controller.invoke(message1);
        Assert.assertNotNull(response);
        Assert.assertEquals(message1.getRequestObject(), response.getResponseObject());
        Assert.assertEquals(message1.getId(), response.getId());
        Assert.assertEquals(response.getResponseCode(), SocketKeys.RESPONSE_CODE_SUCCESS);
    }

    @Test
    public void testInvokeFailure() {
        ServiceRequest message1 = new ServiceRequest();
        message1.setId("11111111111111111111111111111122");
        message1.setRequestObject("test string");
        message1.setServiceId("1000010002");
        message1.setTransformType(SocketKeys.TRANSFORM_JSON);
        ServiceResponse response = controller.invoke(message1);
        Assert.assertNotNull(response);
        Assert.assertEquals(message1.getId(), response.getId());
        Assert.assertEquals(response.getResponseCode(), SocketKeys.RESPONSE_CODE_SERVICEERROR);
        Assert.assertEquals(response.getResponseMessage(), "test error!");
    }

}
