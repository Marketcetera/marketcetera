package org.marketcetera.orderloader.fix;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.orderloader.OrderParsingException;

import java.math.BigDecimal;

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
        CustomField cf = new CustomField<String>(1, null);
        assertEquals(42, cf.parseMessageValue("42")); //$NON-NLS-1$
        assertEquals(Integer.class, cf.parseMessageValue("42").getClass()); //$NON-NLS-1$

        assertEquals(new BigDecimal("42.24"), cf.parseMessageValue("42.24")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(BigDecimal.class, cf.parseMessageValue("42.24").getClass()); //$NON-NLS-1$

        assertEquals("toli kuznets", cf.parseMessageValue("toli kuznets")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testGetCustomField() throws Exception
    {
        assertEquals(new CustomField<String>(123, null), CustomField.getCustomField("123")); //$NON-NLS-1$
        (new ExpectedTestFailure(OrderParsingException.class, "not123") { //$NON-NLS-1$
            protected void execute() throws Throwable
            {
                CustomField.getCustomField("not123"); //$NON-NLS-1$
            }}).run();
    }
}
