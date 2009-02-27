package org.marketcetera.photon;

import junit.framework.TestCase;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.Side;
import quickfix.field.TimeInForce;

public class FIXFieldLocalizerTest extends TestCase {
	public void testGetLocalizedFIXValueName() throws Exception {
		assertEquals("LMT", FIXFieldLocalizer.getLocalizedFIXValueName(OrdType.class.getSimpleName(), "LIMIT"));

		assertEquals("NEW", FIXFieldLocalizer.getLocalizedFIXValueName(OrdStatus.class.getSimpleName(), "NEW"));
		assertEquals("PARTIAL", FIXFieldLocalizer.getLocalizedFIXValueName(OrdStatus.class.getSimpleName(), "PARTIALLY FILLED"));
	}
	
	
    /** Verify that extract value works for special cases like Side and OrdType */
    public void testExtractValue_Side() throws Exception {
        assertEquals("Side not shortened correctly", "B", FIXFieldLocalizer.getLocalizedFIXValueName(Side.class.getSimpleName(), "BUY"));
        assertEquals("Side not shortened correctly", "S", FIXFieldLocalizer.getLocalizedFIXValueName(Side.class.getSimpleName(), "SELL"));
        assertEquals("Side not shortened correctly", "SS", FIXFieldLocalizer.getLocalizedFIXValueName(Side.class.getSimpleName(), "SELL SHORT"));
    }

    /** Verify that extract value works for special cases like Side and OrdType */
    public void testExtractValue_OrdType() throws Exception {

        assertEquals("OrdType not shortened correctly", "LOC", FIXFieldLocalizer.getLocalizedFIXValueName(OrdType.class.getSimpleName(), "LIMIT ON CLOSE"));
        assertEquals("OrdType not shortened correctly", "MOC", FIXFieldLocalizer.getLocalizedFIXValueName(OrdType.class.getSimpleName(), "MARKET ON CLOSE"));
        assertEquals("OrdType not shortened correctly", "MKT", FIXFieldLocalizer.getLocalizedFIXValueName(OrdType.class.getSimpleName(), "MARKET"));
        assertEquals("OrdType not shortened correctly", "LMT", FIXFieldLocalizer.getLocalizedFIXValueName(OrdType.class.getSimpleName(), "LIMIT"));
        assertEquals("OrdType not shortened correctly", "FX_LMT", FIXFieldLocalizer.getLocalizedFIXValueName(OrdType.class.getSimpleName(), "FOREX_LIMIT"));
        assertEquals("OrdType not shortened correctly", "FX_MKT", FIXFieldLocalizer.getLocalizedFIXValueName(OrdType.class.getSimpleName(), "FOREX_MARKET"));
        assertEquals("OrdType not shortened correctly", "FUNARI", FIXFieldLocalizer.getLocalizedFIXValueName(OrdType.class.getSimpleName(), "FUNARI"));

    }

    /** Verify that extract value works for special cases like Side and OrdStatus */
    public void testExtractValue_OrdStatus() throws Exception {
        assertEquals("OrdStatus not shortened correctly", "PARTIAL", FIXFieldLocalizer.getLocalizedFIXValueName(OrdStatus.class.getSimpleName(), "PARTIALLY FILLED"));
        assertEquals("OrdStatus not shortened correctly", "PEND CANC", FIXFieldLocalizer.getLocalizedFIXValueName(OrdStatus.class.getSimpleName(), "PENDING CANCEL"));
        assertEquals("OrdStatus not shortened correctly", "PEND REPL", FIXFieldLocalizer.getLocalizedFIXValueName(OrdStatus.class.getSimpleName(), "PENDING REPLACE"));

        assertEquals("OrdStatus not falling through correctly", "ACCEPTED FOR BIDDING", FIXFieldLocalizer.getLocalizedFIXValueName(OrdStatus.class.getSimpleName(), "ACCEPTED FOR BIDDING"));
    }

    /** Verify that extract value works for special cases like Side and OrdType */
    public void testExtractValue_TIF() throws Exception {

        assertEquals("TIF not shortened correctly", "CLO", FIXFieldLocalizer.getLocalizedFIXValueName(TimeInForce.class.getSimpleName(), "AT THE CLOSE"));
        assertEquals("TIF not shortened correctly", "DAY", FIXFieldLocalizer.getLocalizedFIXValueName(TimeInForce.class.getSimpleName(), "DAY"));
        assertEquals("TIF not shortened correctly", "GTC", FIXFieldLocalizer.getLocalizedFIXValueName(TimeInForce.class.getSimpleName(), "GOOD TILL CANCEL"));
        assertEquals("TIF not shortened correctly", "FOK", FIXFieldLocalizer.getLocalizedFIXValueName(TimeInForce.class.getSimpleName(), "FILL OR KILL"));
        assertEquals("TIF not shortened correctly", "OPG", FIXFieldLocalizer.getLocalizedFIXValueName(TimeInForce.class.getSimpleName(), "AT THE OPENING"));

    }

}
