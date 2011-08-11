/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.filter;

import com.thoughtworks.xstream.XStream;

/**
 * <p>
 * 描述:Xml解析类
 * </p>
 *
 * @see
 * @author yangjun2
 * @email yangjun1120@gmail.com
 *
 */
public class XmlTextParser implements ITextProtocolParser {
    private static XStream xstream = new XStream();

    //    static {
    //        xstream.alias("m", ISocketMessage.class);
    //    }

    @Override
    public String to(final Object obj) {
        Object tObj = obj;
        return xstream.toXML(tObj);
    }

    @Override
    public <T> T from(final String s) {
        String ts = s;
        return (T) xstream.fromXML(ts);
    }

}
