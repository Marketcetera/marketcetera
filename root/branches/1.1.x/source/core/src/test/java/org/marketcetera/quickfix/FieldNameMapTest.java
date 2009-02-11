package org.marketcetera.quickfix;

import junit.framework.Test;
import junit.framework.TestCase;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.core.ClassVersion;
import quickfix.field.OrdStatus;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class FieldNameMapTest extends TestCase {
    public FieldNameMapTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(FieldNameMapTest.class);
    }

    public void testMapOrdStatus() throws Exception {
        FieldNameMap<Character> map = new FieldNameMap<Character>(OrdStatus.FIELD,OrdStatus.class);
        assertEquals(OrdStatus.ACCEPTED_FOR_BIDDING, (char)map.getValue("Accepted For Bidding")); //$NON-NLS-1$
        assertEquals(OrdStatus.CALCULATED, (char)map.getValue("Calculated")); //$NON-NLS-1$
        assertEquals(OrdStatus.CANCELED, (char)map.getValue("Canceled")); //$NON-NLS-1$
        assertEquals(OrdStatus.DONE_FOR_DAY, (char)map.getValue("Done For Day")); //$NON-NLS-1$
        assertEquals(OrdStatus.EXPIRED, (char)map.getValue("Expired")); //$NON-NLS-1$
        assertEquals(OrdStatus.FILLED, (char)map.getValue("Filled")); //$NON-NLS-1$
        assertEquals(OrdStatus.NEW, (char)map.getValue("New")); //$NON-NLS-1$
        assertEquals(OrdStatus.PARTIALLY_FILLED, (char)map.getValue("Partially Filled")); //$NON-NLS-1$
        assertEquals(OrdStatus.PENDING_CANCEL, (char)map.getValue("Pending Cancel")); //$NON-NLS-1$
        assertEquals(OrdStatus.PENDING_NEW, (char)map.getValue("Pending New")); //$NON-NLS-1$
        assertEquals(OrdStatus.PENDING_REPLACE, (char)map.getValue("Pending Replace")); //$NON-NLS-1$
        assertEquals(OrdStatus.REJECTED, (char)map.getValue("Rejected")); //$NON-NLS-1$
        assertEquals(OrdStatus.REPLACED, (char)map.getValue("Replaced")); //$NON-NLS-1$
        assertEquals(OrdStatus.STOPPED, (char)map.getValue("Stopped")); //$NON-NLS-1$
        assertEquals(OrdStatus.SUSPENDED, (char)map.getValue("Suspended")); //$NON-NLS-1$

        assertEquals("Accepted For Bidding", map.getName((char)OrdStatus.ACCEPTED_FOR_BIDDING)); //$NON-NLS-1$
        assertEquals("Calculated", map.getName((char)OrdStatus.CALCULATED)); //$NON-NLS-1$
        assertEquals("Canceled", map.getName((char)OrdStatus.CANCELED)); //$NON-NLS-1$
        assertEquals("Done For Day", map.getName((char)OrdStatus.DONE_FOR_DAY)); //$NON-NLS-1$
        assertEquals("Expired", map.getName((char)OrdStatus.EXPIRED)); //$NON-NLS-1$
        assertEquals("Filled", map.getName((char)OrdStatus.FILLED)); //$NON-NLS-1$
        assertEquals("New", map.getName((char)OrdStatus.NEW)); //$NON-NLS-1$
        assertEquals("Partially Filled", map.getName((char)OrdStatus.PARTIALLY_FILLED)); //$NON-NLS-1$
        assertEquals("Pending Cancel", map.getName((char)OrdStatus.PENDING_CANCEL)); //$NON-NLS-1$
        assertEquals("Pending New", map.getName((char)OrdStatus.PENDING_NEW)); //$NON-NLS-1$
        assertEquals("Filled", map.getName((char)OrdStatus.FILLED)); //$NON-NLS-1$
        assertEquals("Pending Replace", map.getName((char)OrdStatus.PENDING_REPLACE)); //$NON-NLS-1$
        assertEquals("Rejected", map.getName((char)OrdStatus.REJECTED)); //$NON-NLS-1$
        assertEquals("Stopped", map.getName((char)OrdStatus.STOPPED)); //$NON-NLS-1$
        assertEquals("Suspended", map.getName((char)OrdStatus.SUSPENDED)); //$NON-NLS-1$

    }

}
