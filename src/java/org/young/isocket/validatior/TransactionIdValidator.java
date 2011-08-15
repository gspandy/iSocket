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
public class TransactionIdValidator {

    public static void validate(String id) {
        Assert.isTrue(id != null && id.length() == SocketKeys.SIZE_TRANSACTION_ID,
                String.format("length of Id:%s must be %s byte!", id, SocketKeys.SIZE_TRANSACTION_ID));
    }

}
