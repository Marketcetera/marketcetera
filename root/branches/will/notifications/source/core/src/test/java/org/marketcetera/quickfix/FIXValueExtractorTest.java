package org.marketcetera.quickfix;

import java.math.BigDecimal;

import junit.framework.Test;

import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.FIXVersionedTestCase;

import quickfix.Group;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.AvgPx;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.CxlQty;
import quickfix.field.ExecID;
import quickfix.field.ExecTransType;
import quickfix.field.ListID;
import quickfix.field.MsgType;
import quickfix.field.NoOrders;
import quickfix.field.NoRpts;
import quickfix.field.OrdStatus;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.RptSeq;
import quickfix.field.Side;
import quickfix.field.Symbol;

public class FIXValueExtractorTest extends FIXVersionedTestCase {

	public FIXValueExtractorTest(String inName, FIXVersion version) {
		super(inName, version);
	}
	
	public static Test suite() {
		return new FIXVersionTestSuite(FIXValueExtractorTest.class,FIXVersion.values());
	}

	public void testExtractHumanValue() throws Exception {
		FIXValueExtractor extractor = new FIXValueExtractor(fixDD.getDictionary(), msgFactory);
		Message message = msgFactory.createMessage(MsgType.EXECUTION_REPORT);
		message.setField(new OrderID("1")); //$NON-NLS-1$
		message.setField(new ExecID("2")); //$NON-NLS-1$
		message.setField(new ExecTransType(ExecTransType.NEW));
		message.setField(new OrdStatus(OrdStatus.FILLED));
		message.setField(new Symbol("R")); //$NON-NLS-1$
		message.setField(new Side(Side.BUY));
		message.setField(new OrderQty(new BigDecimal("123"))); //$NON-NLS-1$
		message.setField(new CumQty(new BigDecimal("124"))); //$NON-NLS-1$
		message.setField(new AvgPx(new BigDecimal("123.4"))); //$NON-NLS-1$

		assertEquals("1", extractor.extractValue(message, OrderID.FIELD, null, null, null).toString()); //$NON-NLS-1$
		assertEquals("2", extractor.extractValue(message, ExecID.FIELD, null, null, null).toString()); //$NON-NLS-1$
		if (fixVersion!=FIXVersion.FIX44){
			assertEquals("NEW", extractor.extractValue(message, ExecTransType.FIELD, null, null, null, true)); //$NON-NLS-1$
		}
		assertEquals("FILLED", extractor.extractValue(message, OrdStatus.FIELD, null, null, null, true)); //$NON-NLS-1$
		assertEquals("R", extractor.extractValue(message, Symbol.FIELD, null, null, null)); //$NON-NLS-1$
		assertEquals(Side.BUY, ((String)extractor.extractValue(message, Side.FIELD, null, null, null)).charAt(0));
		assertEquals("123", ((BigDecimal)extractor.extractValue(message, OrderQty.FIELD, null, null, null)).toPlainString()); //$NON-NLS-1$
		assertEquals("124", ((BigDecimal)extractor.extractValue(message, CumQty.FIELD, null, null, null)).toPlainString()); //$NON-NLS-1$
		assertEquals("123.4", ((BigDecimal)extractor.extractValue(message, AvgPx.FIELD, null, null, null)).toPlainString()); //$NON-NLS-1$

	}

	public void testExtractValue() throws Exception {
		FIXValueExtractor extractor = new FIXValueExtractor(fixDD.getDictionary(), msgFactory);
		Message message = msgFactory.createMessage(MsgType.LIST_STATUS);
		message.setField(new ListID("1")); //$NON-NLS-1$
		message.setField(new NoRpts(11));
		message.setField(new RptSeq(7));
		
		Group group = msgFactory.createGroup(MsgType.LIST_STATUS, NoOrders.FIELD);
		group.setField(new ClOrdID("1")); //$NON-NLS-1$
		group.setField(new CumQty(new BigDecimal("123"))); //$NON-NLS-1$
		group.setField(new CxlQty(new BigDecimal("123.400"))); //$NON-NLS-1$
		group.setField(new AvgPx(new BigDecimal("123.40"))); //$NON-NLS-1$
		message.addGroup(group);
		group = msgFactory.createGroup(MsgType.LIST_STATUS, NoOrders.FIELD);
		group.setField(new ClOrdID("2")); //$NON-NLS-1$
		group.setField(new CumQty(new BigDecimal("124"))); //$NON-NLS-1$
		group.setField(new CxlQty(new BigDecimal("124.400"))); //$NON-NLS-1$
		group.setField(new AvgPx(new BigDecimal("124.40"))); //$NON-NLS-1$
		message.addGroup(group);

		assertEquals("1", extractor.extractValue(message, ListID.FIELD, null, null, null).toString()); //$NON-NLS-1$
		assertEquals("11", extractor.extractValue(message, NoRpts.FIELD, null, null, null).toString()); //$NON-NLS-1$
		assertEquals("7", extractor.extractValue(message, RptSeq.FIELD, null, null, null).toString()); //$NON-NLS-1$

		assertEquals("123", ((BigDecimal)extractor.extractValue(message, CumQty.FIELD, NoOrders.FIELD, ClOrdID.FIELD, "1")).toPlainString()); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("123.400", ((BigDecimal)extractor.extractValue(message, CxlQty.FIELD, NoOrders.FIELD, ClOrdID.FIELD, "1")).toPlainString()); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("123.40", ((BigDecimal)extractor.extractValue(message, AvgPx.FIELD, NoOrders.FIELD, ClOrdID.FIELD, "1")).toPlainString()); //$NON-NLS-1$ //$NON-NLS-2$

		assertEquals("124", ((BigDecimal)extractor.extractValue(message, CumQty.FIELD, NoOrders.FIELD, ClOrdID.FIELD, "2")).toPlainString()); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("124.400", ((BigDecimal)extractor.extractValue(message, CxlQty.FIELD, NoOrders.FIELD, ClOrdID.FIELD, "2")).toPlainString()); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("124.40", ((BigDecimal)extractor.extractValue(message, AvgPx.FIELD, NoOrders.FIELD, ClOrdID.FIELD, "2")).toPlainString()); //$NON-NLS-1$ //$NON-NLS-2$
}

}
