package org.marketcetera.webservices.systemmodel;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;

/* $License$ */

/**
 * Tests {@link WebServicesString}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class WebServicesStringTest
{
    /**
     * Run once before all tests.
     */
    @BeforeClass
    public static void once()
    {
        LoggerConfiguration.logSetup();
    }
    /**
     * Tests the XML adapter used for web services tring values.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testXmlAdapter()
            throws Exception
    {
        WebServicesString.WebServicesStringAdapter adapter = new WebServicesString.WebServicesStringAdapter();
        String[] testValues = new String[] { "this is a test value",
                                             "    this is a test value    ",
                                             "<?xml version=\"1.0\" encoding=\"utf-8\"?><xml_Doc><doc_part1>abc</doc_part1></xml_Doc>",
                                             "  ![CDATA[ this is a test value ]]  "};
        for(String testValue : testValues) {
            String marshalledValue = adapter.marshal(testValue); 
            assertEquals("![CDATA[" + testValue + "]]",
                         marshalledValue);
            assertEquals(testValue,
                         adapter.unmarshal(marshalledValue));
        }
    }
}
