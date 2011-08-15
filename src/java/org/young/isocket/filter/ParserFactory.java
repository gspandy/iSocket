/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isocket.filter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
public class ParserFactory {
    private static Map<String, ITextProtocolParser> parserMap = Collections
            .synchronizedMap(new HashMap<String, ITextProtocolParser>());

    public ParserFactory() {
        parserMap.put(SocketKeys.TRANSFORM_JSON, new JsonTextParser());
        parserMap.put(SocketKeys.TRANSFORM_XML, new XmlTextParser());
    }

    public ITextProtocolParser getParser(String type) {
        return parserMap.get(type);

    }
}
