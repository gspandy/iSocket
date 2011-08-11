/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.parse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.young.icore.util.ClassLoaderUtils;
import org.young.isokcet.exception.ConfigException;

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
abstract public class AbstractXmlConfigParser<T> implements IXmlConfigParser {

    private static final Logger logger = LoggerFactory.getLogger(AbstractXmlConfigParser.class);

    abstract protected void doStartElement(XMLStreamReader r, Map<String, T> m);

    abstract protected void doEndElement(XMLStreamReader r, Map<String, T> m);

    public final Map<String, T> parse(final String fileLocations) {
        Map<String, T> map = new HashMap<String, T>();
        String[] locations = StringUtils.split(fileLocations, ",");
        XMLInputFactory factory = XMLInputFactory.newInstance();
        ClassLoader defaultClassLoader = ClassLoaderUtils.getDefaultClassLoader();
        for (String s : locations) {
            String ns = s;

            if (ns.isEmpty())
                continue;

            XMLStreamReader r = null;
            FileReader fr = null;
            BufferedReader br = null;
            try {

                fr = new FileReader(defaultClassLoader.getResource(ns).getFile());
                br = new BufferedReader(fr);

                Stack<Object> stack = new Stack<Object>();

                r = factory.createXMLStreamReader(br);
                int event = r.getEventType();
                while (true) {
                    switch (event) {
                    case XMLStreamConstants.START_ELEMENT:

                        doStartElement(r, map);

                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        doEndElement(r, map);

                        break;

                    default:
                        dummy();

                    }

                    //退出while循环
                    if (!r.hasNext()) {
                        break;
                    }

                    event = r.next();

                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                if (e instanceof ConfigException)
                    throw (ConfigException) e;
                else
                    throw new ConfigException(e.getMessage(), e);
            } finally {

                try {
                    if (r != null) {
                        r.close();
                    }
                } catch (XMLStreamException e) {
                    logger.error(e.getMessage(), e);
                    throw new ConfigException(e.getMessage(), e);
                }

                try {
                    if (fr != null) {
                        fr.close();
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    throw new ConfigException(e.getMessage(), e);
                }

                try {
                    if (br != null) {
                        br.close();
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    throw new ConfigException(e.getMessage(), e);
                }
            }
        }

        return map;

    }

    protected void dummy() {

    }

}
