package org.marketcetera.messagehistory;

import junit.framework.Test;

import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.FIXVersionedTestCase;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.AvgPx;
import quickfix.field.CumQty;
import quickfix.field.LastPx;
import quickfix.field.LastShares;
import quickfix.field.LeavesQty;
import quickfix.field.MsgType;
import quickfix.field.OrderQty;
import quickfix.field.Side;
import quickfix.field.Symbol;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

public class AveragePriceListTest extends FIXVersionedTestCase {

	public AveragePriceListTest(String inName, FIXVersion version) {
		super(inName, version);
	}

	public static Test suite() {
        // we dont' really want to setup multi-versioned tests, but it's the easiest way to create the factories
        return new FIXVersionTestSuite(AveragePriceListTest.class, new FIXVersion[] {FIXVersion.FIX42});
    }

	public void testInsertExecutionReport() throws FieldNotFound {
		EventList<MessageHolder> source = new BasicEventList<MessageHolder>();
		AveragePriceList averagePriceList = new AveragePriceList(this.msgFactory, source);
		Message message = msgFactory.createMessage(MsgType.EXECUTION_REPORT);
		message.setField(new Symbol("IBM")); //$NON-NLS-1$
		message.setField(new Side(Side.BUY));
		message.setField(new CumQty(10.0));
		message.setField(new AvgPx(11.0));
		message.setField(new LeavesQty(90.0));
		message.setField(new LastShares(10.0));
		message.setField(new LastPx(11.0));
		source.add(new IncomingMessageHolder(message));

		assertEquals(1, averagePriceList.size());
		Message avgPriceMessage = averagePriceList.get(0).getMessage();
		assertEquals(MsgType.EXECUTION_REPORT, avgPriceMessage.getHeader().getString(MsgType.FIELD));
		assertEquals(10.0, avgPriceMessage.getDouble(CumQty.FIELD), .00001);
		assertEquals(11.0, avgPriceMessage.getDouble(AvgPx.FIELD), .0001);

		Message message2 = msgFactory.createMessage(MsgType.EXECUTION_REPORT);
		message2.setField(new Symbol("IBM")); //$NON-NLS-1$
		message2.setField(new Side(Side.BUY));
		message2.setField(new CumQty(110.0));
		message2.setField(new AvgPx(111.0));
		message2.setField(new LeavesQty(190.0));
		message2.setField(new LastShares(110.0));
		message2.setField(new LastPx(111.0));
		source.add(new IncomingMessageHolder(message2));

		assertEquals(1, averagePriceList.size());
		avgPriceMessage = averagePriceList.get(0).getMessage();
		assertEquals(MsgType.EXECUTION_REPORT, avgPriceMessage.getHeader().getString(MsgType.FIELD));
		assertEquals(120.0, avgPriceMessage.getDouble(CumQty.FIELD), .00001);
		assertEquals(102.66666, avgPriceMessage.getDouble(AvgPx.FIELD), .0001);
		
		Message message3 = msgFactory.createMessage(MsgType.ORDER_SINGLE);
		message3.setField(new Symbol("IBM")); //$NON-NLS-1$
		message3.setField(new Side(Side.BUY));
		message3.setField(new OrderQty(1000));
		source.add(new IncomingMessageHolder(message3));

		assertEquals(1, averagePriceList.size());
		avgPriceMessage = averagePriceList.get(0).getMessage();
		assertEquals(MsgType.EXECUTION_REPORT, avgPriceMessage.getHeader().getString(MsgType.FIELD));
		assertEquals(120.0, avgPriceMessage.getDouble(CumQty.FIELD), .0001);
		assertEquals(102.66666, avgPriceMessage.getDouble(AvgPx.FIELD), .0001);
		assertEquals(1000.0, avgPriceMessage.getDouble(OrderQty.FIELD), .0001);
		
	}

	public void testAddOrderFirst() throws FieldNotFound {
		EventList<MessageHolder> source = new BasicEventList<MessageHolder>();
		AveragePriceList averagePriceList = new AveragePriceList(this.msgFactory, source);
		Message message = msgFactory.createMessage(MsgType.ORDER_SINGLE);
		message.setField(new Symbol("IBM")); //$NON-NLS-1$
		message.setField(new Side(Side.BUY));
		message.setField(new OrderQty(1000));
		source.add(new IncomingMessageHolder(message));

		assertEquals(1, averagePriceList.size());
		Message avgPriceMessage = averagePriceList.get(0).getMessage();
		assertEquals(MsgType.EXECUTION_REPORT, avgPriceMessage.getHeader().getString(MsgType.FIELD));
		assertEquals(1000.0, avgPriceMessage.getDouble(OrderQty.FIELD), .0001);
		
	}
	
	public void testRemove() {
		final EventList<MessageHolder> source = new BasicEventList<MessageHolder>();
		AveragePriceList averagePriceList = new AveragePriceList(this.msgFactory, source);
		Message message = msgFactory.createMessage(MsgType.EXECUTION_REPORT);
		message.setField(new Symbol("IBM")); //$NON-NLS-1$
		message.setField(new Side(Side.BUY));
		message.setField(new CumQty(10.0));
		message.setField(new AvgPx(11.0));
		message.setField(new LeavesQty(90.0));
		message.setField(new LastShares(10.0));
		message.setField(new LastPx(11.0));
		source.add(new IncomingMessageHolder(message));

		assertEquals(1, averagePriceList.size());

		// not implemented
		new ExpectedTestFailure(UnsupportedOperationException.class){
			@Override
			protected void execute() throws Throwable {
				source.remove(0);
			}
		}.run();
	}

	public void testUpdate() {
		final EventList<MessageHolder> source = new BasicEventList<MessageHolder>();
		AveragePriceList averagePriceList = new AveragePriceList(this.msgFactory, source);
		final Message message = msgFactory.createMessage(MsgType.EXECUTION_REPORT);
		message.setField(new Symbol("IBM")); //$NON-NLS-1$
		message.setField(new Side(Side.BUY));
		message.setField(new CumQty(10.0));
		message.setField(new AvgPx(11.0));
		message.setField(new LeavesQty(90.0));
		message.setField(new LastShares(10.0));
		message.setField(new LastPx(11.0));
		source.add(new IncomingMessageHolder(message));

		assertEquals(1, averagePriceList.size());

		new ExpectedTestFailure(UnsupportedOperationException.class){
			@Override
			protected void execute() throws Throwable {
				source.set(0, new IncomingMessageHolder(message));
			}
		}.run();
	}

}
