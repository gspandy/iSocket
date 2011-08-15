/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isocket.threadpool;

/**
 * <p>
 * 描述:任务接口
 * </p>
 *
 * @see
 * @author yangjun2
 * @email yangjun1120@gmail.com
 *
 */
public interface Job extends Runnable {
    public String getKey();

}
