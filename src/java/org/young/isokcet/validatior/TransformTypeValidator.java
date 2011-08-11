/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.validatior;

import org.young.icore.util.Assert;
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
public class TransformTypeValidator {

    public static void validate(String transformType) {
        Assert.isTrue(
                transformType != null && transformType.equals(SocketKeys.TRANSFORM_JSON)
                        || transformType.equals(SocketKeys.TRANSFORM_XML),
                String.format("tranform type:%s is valid", transformType));

    }

}
