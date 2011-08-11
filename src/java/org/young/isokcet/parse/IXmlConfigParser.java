/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.parse;

import java.util.Map;

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
public interface IXmlConfigParser {
    public <T> Map<String, T> parse(final String fileLocations);
}
