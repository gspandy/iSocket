/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isocket.exception;

import org.young.icore.exception.NestedRuntimeException;

/**
 * <p>
 * 描述:配置的异常类
 * </p>
 *
 * @see
 * @author yangjun2
 * @email yangjun1120@gmail.com
 *
 */
public class ConfigException extends NestedRuntimeException {
    public ConfigException(String msg) {
        super(msg);
    }

    public ConfigException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
