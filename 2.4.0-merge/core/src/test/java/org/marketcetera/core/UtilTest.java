package org.marketcetera.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Properties;

import org.junit.Test;
import org.marketcetera.util.test.UnicodeData;
import org.marketcetera.util.ws.tags.AppId;

/* $License$ */

/**
 * Tests utilties in {@link Util}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
public class UtilTest
{
    /**
     * Tests conversion of <code>String</code> objects to <code>Properties</code> objects.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void propertiesFromString()
        throws Exception
    {
        String testString = null;
        assertNull(Util.propertiesFromString(testString));
        testString = "";
        assertNull(Util.propertiesFromString(testString));
        testString = "key=value";
        Properties expectedResults = new Properties();
        expectedResults.setProperty("key",
                                    "value");
        assertEquals(expectedResults,
                     Util.propertiesFromString(testString));
        testString += ":" + "key=value";
        assertEquals(expectedResults,
                     Util.propertiesFromString(testString));
        expectedResults.setProperty(UnicodeData.HOUSE_AR,
                                    UnicodeData.HELLO_GR);
        testString += ":" + UnicodeData.HOUSE_AR + "=" + UnicodeData.HELLO_GR;
        assertEquals(expectedResults,
                     Util.propertiesFromString(testString));
        expectedResults.setProperty("y",
                                    "value");
        testString += ":" + "ke:y=value";
        assertEquals(expectedResults,
                     Util.propertiesFromString(testString));
        testString += ":" + "nothing";
        assertEquals(expectedResults,
                     Util.propertiesFromString(testString));
        testString += ":" + "ke2=x=value2";
        assertEquals(expectedResults,
                     Util.propertiesFromString(testString));
        assertEquals(Util.propertiesFromString(testString),
                     Util.propertiesFromString(Util.propertiesToString(expectedResults)));
    }
    /**
     * Tests the ability for {@link Util#propertiesFromString(String)} to parse strings that contain
     * the delimiter characters.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void escapedPropertiesFromString()
        throws Exception
    {
        Properties expectedResults = new Properties();
        String testString = "key1=c" + Util.ESCAPED_KEY_VALUE_DELIMITER + Util.ESCAPED_ESCAPE_CHARACTER + "path:a" + Util.ESCAPED_ESCAPE_CHARACTER + "=value2:key3=value3";
        expectedResults.setProperty("key1",
                                    "c:\\path");
        expectedResults.setProperty("a" + Util.ESCAPE_CHARACTER,
                                    "value2");
        expectedResults.setProperty("key3",
                                    "value3");
        assertEquals(expectedResults,
                     Util.propertiesFromString(testString));
        testString = "key4=value" + Util.ESCAPED_KEY_VALUE_SEPARATOR + "something" + Util.ESCAPED_KEY_VALUE_DELIMITER;
        expectedResults.clear();
        expectedResults.setProperty("key4",
                                    "value=something:");
        assertEquals(expectedResults,
                     Util.propertiesFromString(testString));
        assertEquals(Util.propertiesFromString(testString),
                     Util.propertiesFromString(Util.propertiesToString(expectedResults)));
    }
    /**
     * Tests a properties string that contains multiple instances of an escaped delimiter/separator. 
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void multipleTokenReplace()
        throws Exception
    {
        StringBuilder propertiesBuilder = new StringBuilder();
        Properties expectedResults = new Properties();
        for(int keyCounter=0;keyCounter<=100;keyCounter++) {
            String key = "key" + Util.ESCAPED_KEY_VALUE_DELIMITER + keyCounter;
            String value = "value" + Util.ESCAPED_KEY_VALUE_SEPARATOR + keyCounter;
            propertiesBuilder.append(key + Util.KEY_VALUE_SEPARATOR + value + Util.KEY_VALUE_DELIMITER);
            expectedResults.setProperty("key" + Util.KEY_VALUE_DELIMITER  + keyCounter,
                                        "value" + Util.KEY_VALUE_SEPARATOR + keyCounter);
        }
        assertEquals(expectedResults,
                     Util.propertiesFromString(propertiesBuilder.toString()));
        assertEquals(Util.propertiesFromString(propertiesBuilder.toString()),
                     Util.propertiesFromString(Util.propertiesToString(expectedResults)));
    }
    /**
     * Tests a scenario where the token to be used to replace a separator conflicts with the contents of the properties. 
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void tokenConflict()
        throws Exception
    {
        StringBuilder propertiesBuilder = new StringBuilder();
        Properties expectedResults = new Properties();
        String key = "key1";
        String value = "value" + "$TOKEN-1$" + Util.ESCAPED_KEY_VALUE_DELIMITER;
        propertiesBuilder.append(key).append(Util.KEY_VALUE_SEPARATOR).append(value);
        expectedResults.setProperty(key,
                                    value.replace(Util.ESCAPED_KEY_VALUE_DELIMITER,
                                                  Util.KEY_VALUE_DELIMITER));
        assertEquals(expectedResults,
                     Util.propertiesFromString(propertiesBuilder.toString()));
        assertEquals(Util.propertiesFromString(propertiesBuilder.toString()),
                     Util.propertiesFromString(Util.propertiesToString(expectedResults)));
    }
    /**
     * Tests an edge condition that requires escaping the escape character.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void annoyingButCorrectExampleFromToli()
        throws Exception
    {
        StringBuilder propertiesBuilder = new StringBuilder();
        Properties expectedResults = new Properties();
        String key1 = "a";
        String value1 = Util.ESCAPED_ESCAPE_CHARACTER;
        String key2 = "b";
        String value2 = Util.ESCAPED_ESCAPE_CHARACTER;
        String key3 = "c";
        String value3 = "2";
        propertiesBuilder.append(key1).append(Util.KEY_VALUE_SEPARATOR).append(value1).append(Util.KEY_VALUE_DELIMITER)
                         .append(key2).append(Util.KEY_VALUE_SEPARATOR).append(value2).append(Util.KEY_VALUE_DELIMITER)
                         .append(key3).append(Util.KEY_VALUE_SEPARATOR).append(value3);
        expectedResults.setProperty(key1,
                                    Util.ESCAPE_CHARACTER);
        expectedResults.setProperty(key2,
                                    Util.ESCAPE_CHARACTER);
        expectedResults.setProperty(key3,
                                    value3);
        assertEquals(expectedResults,
                     Util.propertiesFromString(propertiesBuilder.toString()));
        assertEquals(Util.propertiesFromString(propertiesBuilder.toString()),
                     Util.propertiesFromString(Util.propertiesToString(expectedResults)));
    }
    /**
     * Tests conversion of <code>Properties</code> objects to <code>String</code> objects. 
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void propertiesToString()
        throws Exception
    {
        // this test works a little differently because the order of key/value pairs in the output is not guaranteed
        // therefore, equality must be tested using a round-trip
        Properties testProperties = new Properties();
        String expectedResults = null;
        assertNull(Util.propertiesToString(null));
        assertNull(Util.propertiesToString(testProperties));
        // single property
        testProperties.setProperty("key",
                                   "value");
        expectedResults = "key=value";
        assertEquals(Util.propertiesFromString(expectedResults),
                     Util.propertiesFromString(Util.propertiesToString(testProperties)));
        // duplicate property
        testProperties.setProperty("key",
                                   "value");
        assertEquals(Util.propertiesFromString(expectedResults),
                     Util.propertiesFromString(Util.propertiesToString(testProperties)));
        // non-ASCII
        testProperties.setProperty(UnicodeData.HOUSE_AR,
                                    UnicodeData.HELLO_GR);
        expectedResults += ":" + UnicodeData.HOUSE_AR + "=" + UnicodeData.HELLO_GR;
        assertEquals(Util.propertiesFromString(expectedResults),
                     Util.propertiesFromString(Util.propertiesToString(testProperties)));
        // escaped values
        expectedResults += ":" + "escapedKey\\:\\==x\\=\\:value2";
        testProperties.setProperty("escapedKey:=",
                                   "x=:value2");
        assertEquals(Util.propertiesFromString(expectedResults),
                     Util.propertiesFromString(Util.propertiesToString(testProperties)));
        // malformed key
        testProperties.setProperty("y",
                                    "value");
        expectedResults += ":" + "ke:y=value";
        assertEquals(Util.propertiesFromString(expectedResults),
                     Util.propertiesFromString(Util.propertiesToString(testProperties)));
        // missing value
        expectedResults += ":" + "nothing";
        assertEquals(Util.propertiesFromString(expectedResults),
                     Util.propertiesFromString(Util.propertiesToString(testProperties)));
        // malformed value
        expectedResults += ":" + "ke2=x=value2";
        assertEquals(Util.propertiesFromString(expectedResults),
                     Util.propertiesFromString(Util.propertiesToString(testProperties)));
    }
    /**
     * Tests the round-trip of an empty value through {@link Util#propertiesFromString(String)}
     * and {@link Util#propertiesToString(Properties)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testEmptyValue()
            throws Exception
    {
        Properties p = new Properties();
        p.setProperty("key",
                      "");
        assertEquals("",
                     Util.propertiesFromString(Util.propertiesToString(p)).getProperty("key"));
    }
    /**
     * Tests {@link Util#getAppId(String, String)}.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void getAppId() throws Exception {
        assertEquals(new AppId("x/y"),Util.getAppId("x","y"));
        assertEquals(new AppId("x/"),Util.getAppId("x",""));
        assertEquals(new AppId("/y"),Util.getAppId("","y"));
        assertEquals(new AppId("/"),Util.getAppId("",""));
        assertEquals(new AppId("null/y"),Util.getAppId(null,"y"));
        assertEquals(new AppId("x/null"),Util.getAppId("x",null));
        assertEquals(new AppId("null/null"),Util.getAppId(null,null));
        assertEquals(new AppId(UnicodeData.COMBO + "/" + UnicodeData.COMBO),
                Util.getAppId(UnicodeData.COMBO,UnicodeData.COMBO));
    }

    /**
     * Tests {@link Util#getVersion(org.marketcetera.util.ws.tags.AppId)}.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void getVersion() throws Exception {
        assertNull(Util.getVersion(null));
        assertNull(Util.getVersion(new AppId(null)));
        assertNull(Util.getVersion(new AppId("any")));
        assertNull(Util.getVersion(Util.getAppId("","")));
        assertNull(Util.getVersion(Util.getAppId("x","")));
        assertEquals("x",Util.getVersion(Util.getAppId("","x")));
        assertEquals(" x ",Util.getVersion(Util.getAppId(""," x ")));
        assertEquals(" x ",Util.getVersion(Util.getAppId(" y "," x ")));
        assertEquals(UnicodeData.COMBO,Util.getVersion(
                Util.getAppId(UnicodeData.COMBO,UnicodeData.COMBO)));
        assertEquals("1.5.0",
                     Util.getVersion(Util.getAppId("Weird",
                                                   "1.5.0")));
    }

    /**
     * Tests {@link Util#getName(org.marketcetera.util.ws.tags.AppId)}.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void getName() throws Exception {
        assertNull(Util.getName(null));
        assertNull(Util.getName(new AppId(null)));
        assertNull(Util.getName(new AppId("")));
        assertEquals(" ", Util.getName(new AppId(" ")));
        assertEquals("any", Util.getName(new AppId("any")));
        assertEquals(" ", Util.getName(Util.getAppId(" ", "")));
        assertEquals(" ", Util.getName(Util.getAppId(" ", " ")));
        assertEquals("x", Util.getName(Util.getAppId("x", "")));
        assertEquals("x", Util.getName(Util.getAppId("x", " ")));
        assertEquals("x", Util.getName(Util.getAppId("x", "y")));
        assertEquals(" x ", Util.getName(Util.getAppId(" x ", " y ")));
        assertEquals(UnicodeData.COMBO, Util.getName(
                Util.getAppId(UnicodeData.COMBO, UnicodeData.COMBO)));
        assertEquals("MyApp", Util.getName(Util.getAppId("MyApp",
                                                         "2.0.0")));
    }
}
