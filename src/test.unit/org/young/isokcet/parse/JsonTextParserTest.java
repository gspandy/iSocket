/*
 * <p>
 * 版权: ©2011 
 * </p>
 */
package org.young.isokcet.parse;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.young.isokcet.filter.JsonTextParser;

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
public class JsonTextParserTest {

    private static JsonTextParser parser;

    @BeforeClass
    public static void beforeClass() {
        parser = new JsonTextParser();
    }

    @AfterClass
    public static void afterClass() {
        parser = null;
    }

    @Test
    public void testParse() {
        Map<String, Date> map = new HashMap<String, Date>();
        map.put("test1", new Date());
        String s = parser.to(map);
        Map<String, Date> map1 = parser.from(s);
        Assert.assertEquals(map1, map);
    }

}
