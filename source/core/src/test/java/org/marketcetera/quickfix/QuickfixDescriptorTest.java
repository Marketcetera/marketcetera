package org.marketcetera.quickfix;

import junit.framework.Test;
import junit.framework.TestCase;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraTestSuite;

/**
 * This is mostly a dummy. make sure we can create one, so that
 * the unit test fails first if code changes
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class QuickfixDescriptorTest extends TestCase
{
    public QuickfixDescriptorTest(String inName)
    {
        super(inName);
    }

    public static Test suite()
    {
        return new MarketceteraTestSuite(QuickfixDescriptorTest.class);
    }

    public void testCreateDescriptor() throws Exception
    {
        QuickFIXDescriptor desc1 = new QuickFIXDescriptor("fix41", "41", "bla");
        QuickFIXDescriptor desc1copy = new QuickFIXDescriptor("fix41", "41", "bla");
        QuickFIXDescriptor desc2 = new QuickFIXDescriptor("fix42", "42", "bob");

        assertEquals(desc1, desc1copy);
        assertFalse(desc1.equals(desc2));
        assertTrue(desc1.equals(desc1copy));
        assertTrue(desc1copy.equals(desc1));
    }

    public void testGetFieldDescriptor() throws Exception
    {
        QuickFIXDescriptor desc1 = new QuickFIXDescriptor("fix41", "41", "bla");
        assertNotNull(desc1);
        assertEquals("41", desc1.getFixVers());
        assertEquals("quickfix.fix41.MessageFactory", desc1.getMessageFactory().getClass().getName());
    }
}
