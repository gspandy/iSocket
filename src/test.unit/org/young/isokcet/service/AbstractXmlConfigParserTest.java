/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.service;

import java.util.Map;

import javax.xml.stream.XMLStreamReader;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.young.isokcet.parse.AbstractXmlConfigParser;

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
public class AbstractXmlConfigParserTest {

    private static AbstractXmlConfigParser parser;
    private String config1 = "services.xml";
    private String config2 = "services.xml,";

    @BeforeClass
    public static void beforeClass() {
        parser = new AbstractXmlConfigParser<ISocketService>() {
            protected void doStartElement(XMLStreamReader r, Map<String, ISocketService> m) {

            }

            protected void doEndElement(XMLStreamReader r, Map<String, ISocketService> m) {

            }
        };
    }

    @AfterClass
    public static void afterClass() {
        parser = null;
    }

    @Test
    public void testPath() {

        try {
            if (parser == null) {
                Assert.fail("AbstractXmlConfigParser create error!");
            }

            parser.parse(config1);
            parser.parse(config2);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

}
