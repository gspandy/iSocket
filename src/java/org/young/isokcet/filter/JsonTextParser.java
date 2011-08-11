/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.filter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

/**
 * <p>
 * 描述:Json解析类
 * </p>
 *
 * @see
 * @author yangjun2
 * @email yangjun1120@gmail.com
 *
 */
public class JsonTextParser implements ITextProtocolParser {
    //    private static ObjectMapper mapper = new ObjectMapper();
    //
    //    static {
    //        mapper.configure(DeserializationConfig.Feature.USE_BIG_DECIMAL_FOR_FLOATS, true);
    //    }
    //
    //    @Override
    //    public String parse(Object obj) throws IOException {
    //        Object tObj = obj;
    //
    //        return mapper.writeValueAsString(tObj);
    //    }

    private static XStream xstream = new XStream(new JettisonMappedXmlDriver());

    static {
        xstream.setMode(XStream.NO_REFERENCES);
        // xstream.alias("m", ISocketMessage.class);
    }

    @Override
    public <T> String to(final T obj) {
        T tObj = obj;
        return xstream.toXML(tObj);
    }

    @Override
    public <T> T from(final String s) {
        String ts = s;
        return (T) xstream.fromXML(ts);
    }

}
