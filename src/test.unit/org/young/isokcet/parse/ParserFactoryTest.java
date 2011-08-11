/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.parse;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.young.isokcet.filter.ITextProtocolParser;
import org.young.isokcet.filter.JsonTextParser;
import org.young.isokcet.filter.ParserFactory;
import org.young.isokcet.filter.XmlTextParser;
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
public class ParserFactoryTest {
    private static ParserFactory factory;

    @BeforeClass
    public static void beforeClass() {
        factory = new ParserFactory();
    }

    @AfterClass
    public static void afterClass() {
        factory = null;
    }

    @Test
    public void testGetParser() {
        ITextProtocolParser parser1 = factory.getParser(SocketKeys.TRANSFORM_JSON);
        ITextProtocolParser parser2 = factory.getParser(SocketKeys.TRANSFORM_XML);
        Assert.assertTrue(parser1 != null && parser1 instanceof JsonTextParser);
        Assert.assertTrue(parser2 != null && parser2 instanceof XmlTextParser);
    }
}
