/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isocket.validatior;

import org.young.icore.util.Assert;
import org.young.isocket.util.SocketKeys;

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
public class ObjectSizeValidator {

    public static void validate(String serializedObj) {
        Assert.isTrue((serializedObj != null) && (serializedObj.length() < SocketKeys.OBJECT_SERIALIZE_SIZE),
                "对象序列化后的长度超过限制(100M)");
    }
}
