package org.marketcetera.photon.ui;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.views.StockOrderTicket;
import org.marketcetera.messagehistory.MessageHolder;
import org.marketcetera.quickfix.FIXMessageUtilTest;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.eclipse.swt.widgets.Table;
import junit.framework.TestCase;
import quickfix.field.Side;
import quickfix.field.OrdType;
import quickfix.field.TimeInForce;
import quickfix.field.OrdStatus;
import quickfix.Message;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class FIXMessageTableFormatTest extends TestCase {
    private FIXMessageFactory msgFactory = FIXVersion.FIX42.getMessageFactory();


    /** Verify that extract value works for special cases like Side and OrdType */
    public void testExtractValue_Side() throws Exception {
        MockFIXMessageTableFormat<MessageHolder> fixmtf = new MockFIXMessageTableFormat<MessageHolder>(null,
                                StockOrderTicket.ID, MessageHolder.class);

        Message buy = FIXMessageUtilTest.createNOS("IBM", 85.85, 100, Side.BUY, msgFactory);
        assertEquals("Side not shortened correctly", "B", fixmtf.extractValue(Side.FIELD, new MessageHolder(buy), 0));
        assertEquals("Side not shortened correctly", "SS", fixmtf.extractValue(Side.FIELD, new MessageHolder(
                FIXMessageUtilTest.createNOS("IBM", 85.85, 100, Side.SELL_SHORT, msgFactory)), 0));

        // verify no field returns ""
        buy.removeField(Side.FIELD);
        assertNull("empty OrdType not working", fixmtf.extractValue(Side.FIELD, new MessageHolder(buy), 0));
    }

    /** Verify that extract value works for special cases like Side and OrdType */
    public void testExtractValue_OrdType() throws Exception {
        MockFIXMessageTableFormat<MessageHolder> fixmtf = new MockFIXMessageTableFormat<MessageHolder>(null,
                                StockOrderTicket.ID, MessageHolder.class);

        Message buy = FIXMessageUtilTest.createNOS("IBM", 85.85, 100, Side.BUY, msgFactory);
        buy.setField(new OrdType(OrdType.LIMIT_ON_CLOSE));
        assertEquals("OrdType not shortened correctly", "LOC", fixmtf.extractValue(OrdType.FIELD, new MessageHolder(buy), 0));
        buy.setField(new OrdType(OrdType.MARKET_ON_CLOSE));
        assertEquals("OrdType not shortened correctly", "MOC", fixmtf.extractValue(OrdType.FIELD, new MessageHolder(buy), 0));
        buy.setField(new OrdType(OrdType.MARKET));
        assertEquals("OrdType not shortened correctly", "MKT", fixmtf.extractValue(OrdType.FIELD, new MessageHolder(buy), 0));
        buy.setField(new OrdType(OrdType.LIMIT));
        assertEquals("OrdType not shortened correctly", "LIM", fixmtf.extractValue(OrdType.FIELD, new MessageHolder(buy), 0));
        buy.setField(new OrdType(OrdType.FOREX_LIMIT));
        assertEquals("OrdType not shortened correctly", "FX_LIM", fixmtf.extractValue(OrdType.FIELD, new MessageHolder(buy), 0));
        buy.setField(new OrdType(OrdType.FOREX_MARKET));
        assertEquals("OrdType not shortened correctly", "FX_MKT", fixmtf.extractValue(OrdType.FIELD, new MessageHolder(buy), 0));

        // verify no field returns ""
        buy.removeField(OrdType.FIELD);
        assertNull("empty OrdType not working", fixmtf.extractValue(OrdType.FIELD, new MessageHolder(buy), 0));

        // now try something that's not in {@link OrdTypeImage} and make sure we still get something readable back
        buy.setField(new OrdType(OrdType.FUNARI));
        assertEquals("OrdType not falling through correctly", "FUNARI", fixmtf.extractValue(OrdType.FIELD, new MessageHolder(buy), 0));
    }

    /** Verify that extract value works for special cases like Side and OrdStatus */
    public void testExtractValue_OrdStatus() throws Exception {
        MockFIXMessageTableFormat<MessageHolder> fixmtf = new MockFIXMessageTableFormat<MessageHolder>(null,
                                StockOrderTicket.ID, MessageHolder.class);

        Message buy = FIXMessageUtilTest.createNOS("IBM", 85.85, 100, Side.BUY, msgFactory);
        buy.setField(new OrdStatus(OrdStatus.PENDING_CANCEL));
        assertEquals("OrdStatus not shortened correctly", "PEND_CANCEL", fixmtf.extractValue(OrdStatus.FIELD, new MessageHolder(buy), 0));
        buy.setField(new OrdStatus(OrdStatus.PENDING_REPLACE));
        assertEquals("OrdStatus not shortened correctly", "PEND_REPL", fixmtf.extractValue(OrdStatus.FIELD, new MessageHolder(buy), 0));
        buy.setField(new OrdStatus(OrdStatus.PARTIALLY_FILLED));
        assertEquals("OrdStatus not shortened correctly", "PARTIAL", fixmtf.extractValue(OrdStatus.FIELD, new MessageHolder(buy), 0));

        // verify no field returns ""
        buy.removeField(OrdStatus.FIELD);
        assertNull("empty OrdStatus not working", fixmtf.extractValue(OrdStatus.FIELD, new MessageHolder(buy), 0));

        // now try something that's not in {@link OrdTypeImage} and make sure we still get something readable back
        buy.setField(new OrdStatus(OrdStatus.ACCEPTED_FOR_BIDDING));
        assertEquals("OrdStatus not falling through correctly", "ACCEPTED FOR BIDDING",
                fixmtf.extractValue(OrdStatus.FIELD, new MessageHolder(buy), 0));
    }

    /** Verify that extract value works for special cases like Side and OrdType */
    public void testExtractValue_TIF() throws Exception {
        MockFIXMessageTableFormat<MessageHolder> fixmtf = new MockFIXMessageTableFormat<MessageHolder>(null,
                                StockOrderTicket.ID, MessageHolder.class);

        Message buy = FIXMessageUtilTest.createNOS("IBM", 85.85, 100, Side.BUY, msgFactory);
        buy.setField(new TimeInForce(TimeInForce.AT_THE_CLOSE));
        assertEquals("TIF not shortened correctly", "CLO", fixmtf.extractValue(TimeInForce.FIELD, new MessageHolder(buy), 0));
        buy.setField(new TimeInForce(TimeInForce.DAY));
        assertEquals("TIF not shortened correctly", "DAY", fixmtf.extractValue(TimeInForce.FIELD, new MessageHolder(buy), 0));
        buy.setField(new TimeInForce(TimeInForce.GOOD_TILL_CANCEL));
        assertEquals("TIF not shortened correctly", "GTC", fixmtf.extractValue(TimeInForce.FIELD, new MessageHolder(buy), 0));
        buy.setField(new TimeInForce(TimeInForce.FILL_OR_KILL));
        assertEquals("TIF not shortened correctly", "FOK", fixmtf.extractValue(TimeInForce.FIELD, new MessageHolder(buy), 0));
        buy.setField(new TimeInForce(TimeInForce.GOOD_TILL_CANCEL));
        assertEquals("TIF not shortened correctly", "GTC", fixmtf.extractValue(TimeInForce.FIELD, new MessageHolder(buy), 0));
        buy.setField(new TimeInForce(TimeInForce.AT_THE_OPENING));
        assertEquals("TIF not shortened correctly", "OPG", fixmtf.extractValue(TimeInForce.FIELD, new MessageHolder(buy), 0));

        // verify no field returns ""
        buy.removeField(TimeInForce.FIELD);
        assertNull("empty TIF not working", fixmtf.extractValue(TimeInForce.FIELD, new MessageHolder(buy), 0));
    }

    private class MockFIXMessageTableFormat<T> extends FIXMessageTableFormat<T> {
        public MockFIXMessageTableFormat(Table table, final String assignedViewID, Class<T> underlyingClass) {
            super(table, assignedViewID, underlyingClass);
        }

        protected Object extractValue(int fieldNum, T element, int columnIndex) {
            return super.extractValue(fieldNum, element, columnIndex);
        }

        public void updateColumnsFromPreferences() {
            // do nothing
        }
    }
}
