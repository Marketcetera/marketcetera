package org.marketcetera.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Properties;

import org.junit.Test;
import org.marketcetera.util.test.UnicodeData;

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
}
