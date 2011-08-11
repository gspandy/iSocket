/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.threadpool;

import java.util.concurrent.RejectedExecutionException;

/**
 * Exception thrown when a rejects to accept
 * a given task for execution.
 *
 * @author yangjun2
 * @email yangjun1120@gmail.com
 */
public class JobRejectedException extends RejectedExecutionException {

    private static final long serialVersionUID = 4597045330724064199L;

    /**
     * Create a new <code>JobRejectedException</code>
     * with the specified detail message and no root cause.
     * @param msg the detail message
     */
    public JobRejectedException(String msg) {
        super(msg);
    }

    /**
     * Create a new <code>JobRejectedException</code>
     * with the specified detail message and the given root cause.
     * @param msg the detail message
     * @param cause the root cause (usually from using an underlying
     * API such as the <code>java.util.concurrent</code> package)
     * @see java.util.concurrent.RejectedExecutionException
     */
    public JobRejectedException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
