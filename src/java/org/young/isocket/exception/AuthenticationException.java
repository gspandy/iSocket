/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isocket.exception;

import org.young.icore.exception.NestedRuntimeException;

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
public class AuthenticationException extends NestedRuntimeException {
    public AuthenticationException(String msg) {
        super(msg);
    }

    public AuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
