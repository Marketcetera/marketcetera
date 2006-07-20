package org.marketcetera.orderloader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.orderloader.CustomField;
import org.marketcetera.orderloader.OrderParsingException;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class CustomFieldTest extends TestCase
{
    public CustomFieldTest(String inName)
    {
        super(inName);
    }

    public static Test suite()
    {
        return new TestSuite(CustomFieldTest.class);
    }

    public void testParseCustomFieldValue()
    {
        CustomField cf = new CustomField(1, null);
        assertEquals(new Integer(42), cf.parseMessageValue("42"));
        assertEquals(Integer.class, cf.parseMessageValue("42").getClass());

        assertEquals(new Double(42.24), cf.parseMessageValue("42.24"));
        assertEquals(Double.class, cf.parseMessageValue("42.24").getClass());

        assertEquals("toli kuznets", cf.parseMessageValue("toli kuznets"));
    }

    public void testGetCustomField() throws Exception
    {
        assertEquals(new CustomField(123, null), CustomField.getCustomField("123"));
        (new ExpectedTestFailure(OrderParsingException.class, "not123") {
            protected void execute() throws Throwable
            {
                CustomField.getCustomField("not123");
            }}).run();
    }
}
