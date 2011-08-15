/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isocket.filter;

/**
 * <p>
 * 描述:
 * </p>
 *
 * @see
 * @author yangjun2
 * @email yangjun1120@gmail.com
 *
 */
public interface ITextProtocolParser {

    /**
     * 
     * <p>
     * 描述:解析对象成为字符串
     * </p>
     * @param
     * @return
     * @throws
     * @see
     * @since %I%
     */
    public <T> String to(final T obj);

    /**
     * 
     * <p>
     * 描述:将字符串（xml or json）解析为对象
     * </p>
     * @param
     * @return
     * @throws
     * @see
     * @since %I%
     */
    public <T> T from(final String s);
}
