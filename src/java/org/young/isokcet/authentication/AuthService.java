/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import netscape.ldap.LDAPException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.young.icore.util.Assert;
import org.young.isokcet.exception.AuthenticationException;
import org.young.isokcet.service.AbstractService;
import org.young.isokcet.service.ServiceRequest;

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
public class AuthService extends AbstractService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private static AuthenticateDelegate delegate;

    public AuthService() {
        try {
            delegate = new AuthenticateDelegate();
        } catch (LDAPException e) {
            throw new AuthenticationException("AuthenticateDelegate initial error!", e);
        }
    }

    @Override
    public void doInvokeBefore(ServiceRequest svcReq) {

    }

    @Override
    public void doInvokeAfter(ServiceRequest svcReq, Object obj) {

    }

    @Override
    public Object doInvoke(ServiceRequest svcReq) {
        Map<String, String> map = svcReq.getRequestObject();
        Assert.notNull(map, "service request must not be null!");

        String userName = map.get("userName");
        String password = map.get("password");
        boolean flag = false;
        try {
            flag = delegate.authenticate(userName, password);
        } catch (LDAPException e) {
            throw new AuthenticationException("user authenticate error!", e);
        }

        String sessionId = UUID.randomUUID().toString();
        return sessionId;
    }

    private String generateSessionID() {
        return UUID.randomUUID().toString();
    }

    public static void main(String[] args) {
        ServiceRequest req = new ServiceRequest();
        Map<String, String> map = new HashMap<String, String>();
        map.put("userName", "isokcetuser");
        map.put("password", "password");
        req.setRequestObject(map);
        AuthService s = new AuthService();
        System.out.println(s.doInvoke(req));
    }

}
