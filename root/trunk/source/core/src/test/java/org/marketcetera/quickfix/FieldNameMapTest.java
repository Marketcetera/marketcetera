package org.marketcetera.quickfix;

import junit.framework.Test;
import junit.framework.TestCase;
import org.marketcetera.core.MarketceteraTestSuite;
import quickfix.field.OrdStatus;

/**
 * @author Graham Miller
 * @version $Id$
 */
public class FieldNameMapTest extends TestCase {
    public FieldNameMapTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(FieldNameMapTest.class);
    }

    public void testMapOrdStatus() throws Exception {
        FieldNameMap<Character> map = new FieldNameMap<Character>(OrdStatus.FIELD,OrdStatus.class);
        assertEquals(OrdStatus.ACCEPTED_FOR_BIDDING, (char)map.getValue("Accepted For Bidding"));
        assertEquals(OrdStatus.CALCULATED, (char)map.getValue("Calculated"));
        assertEquals(OrdStatus.CANCELED, (char)map.getValue("Canceled"));
        assertEquals(OrdStatus.DONE_FOR_DAY, (char)map.getValue("Done For Day"));
        assertEquals(OrdStatus.EXPIRED, (char)map.getValue("Expired"));
        assertEquals(OrdStatus.FILLED, (char)map.getValue("Filled"));
        assertEquals(OrdStatus.NEW, (char)map.getValue("New"));
        assertEquals(OrdStatus.PARTIALLY_FILLED, (char)map.getValue("Partially Filled"));
        assertEquals(OrdStatus.PENDING_CANCEL, (char)map.getValue("Pending Cancel"));
        assertEquals(OrdStatus.PENDING_NEW, (char)map.getValue("Pending New"));
        assertEquals(OrdStatus.PENDING_REPLACE, (char)map.getValue("Pending Replace"));
        assertEquals(OrdStatus.REJECTED, (char)map.getValue("Rejected"));
        assertEquals(OrdStatus.REPLACED, (char)map.getValue("Replaced"));
        assertEquals(OrdStatus.STOPPED, (char)map.getValue("Stopped"));
        assertEquals(OrdStatus.SUSPENDED, (char)map.getValue("Suspended"));

        assertEquals("Accepted For Bidding", map.getName((char)OrdStatus.ACCEPTED_FOR_BIDDING));
        assertEquals("Calculated", map.getName((char)OrdStatus.CALCULATED));
        assertEquals("Canceled", map.getName((char)OrdStatus.CANCELED));
        assertEquals("Done For Day", map.getName((char)OrdStatus.DONE_FOR_DAY));
        assertEquals("Expired", map.getName((char)OrdStatus.EXPIRED));
        assertEquals("Filled", map.getName((char)OrdStatus.FILLED));
        assertEquals("New", map.getName((char)OrdStatus.NEW));
        assertEquals("Partially Filled", map.getName((char)OrdStatus.PARTIALLY_FILLED));
        assertEquals("Pending Cancel", map.getName((char)OrdStatus.PENDING_CANCEL));
        assertEquals("Pending New", map.getName((char)OrdStatus.PENDING_NEW));
        assertEquals("Filled", map.getName((char)OrdStatus.FILLED));
        assertEquals("Pending Replace", map.getName((char)OrdStatus.PENDING_REPLACE));
        assertEquals("Rejected", map.getName((char)OrdStatus.REJECTED));
        assertEquals("Stopped", map.getName((char)OrdStatus.STOPPED));
        assertEquals("Suspended", map.getName((char)OrdStatus.SUSPENDED));

    }

}
