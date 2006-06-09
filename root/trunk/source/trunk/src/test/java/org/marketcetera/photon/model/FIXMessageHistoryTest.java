package org.marketcetera.photon.model;

import java.math.BigDecimal;
import java.util.Date;

import junit.framework.TestCase;

import org.marketcetera.core.AccessViolator;
import org.marketcetera.core.AccountID;
import org.marketcetera.core.InternalID;
import org.marketcetera.core.MSymbol;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.DateField;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.AvgPx;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.ExecID;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.LastPx;
import quickfix.field.LastShares;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

public class FIXMessageHistoryTest extends TestCase {

	
	protected FIXMessageHistory getMessageHistory(){
		return new FIXMessageHistory();
	}

	/*
	 * Test method for 'org.marketcetera.photon.model.FIXMessageHistory.addIncomingMessage(Message)'
	 */
	public void testAddIncomingMessage() throws FieldNotFound {
		FIXMessageHistory history = getMessageHistory();
		InternalID orderID1 = new InternalID("1");
		InternalID clOrderID1 = new InternalID("2");
		String execID = "3";
		char execTransType = ExecTransType.STATUS;
		char execType = ExecType.PARTIAL_FILL;
		char ordStatus = OrdStatus.PARTIALLY_FILLED;
		char side = Side.SELL_SHORT;
		BigDecimal orderQty = new BigDecimal(1000);
		BigDecimal orderPrice = new BigDecimal(789);
		BigDecimal lastQty = new BigDecimal(100);
		BigDecimal lastPrice = new BigDecimal("12.3");
		BigDecimal leavesQty = new BigDecimal(900);
		BigDecimal cumQty = new BigDecimal(100);
		BigDecimal avgPrice = new BigDecimal("12.3");
		MSymbol symbol = new MSymbol("ASDF");
		

		Message message = FIXMessageUtil.newExecutionReport(orderID1, clOrderID1, execID, execTransType, execType, ordStatus, side, orderQty, orderPrice, lastQty, lastPrice, leavesQty, cumQty, avgPrice, symbol);

		{
			history.addIncomingMessage(message);
			EventList<MessageHolder> historyList = history.getAllMessages();
			assertEquals(1, historyList.size());
			assertEquals(IncomingMessageHolder.class, historyList.get(0).getClass());
			IncomingMessageHolder holder = (IncomingMessageHolder) historyList.get(0);
			Message historyMessage = holder.getMessage();
			assertEquals(orderID1.toString(), historyMessage.getString(OrderID.FIELD));
			assertEquals(clOrderID1.toString(), historyMessage.getString(ClOrdID.FIELD));
			assertEquals(execID, historyMessage.getString(ExecID.FIELD));
			assertEquals(""+execTransType, historyMessage.getString(ExecTransType.FIELD));
			assertEquals(""+execType, historyMessage.getString(ExecType.FIELD));
			assertEquals(""+ordStatus, historyMessage.getString(OrdStatus.FIELD));
			assertEquals(""+side, historyMessage.getString(Side.FIELD));
			assertEquals(orderQty, new BigDecimal(historyMessage.getString(OrderQty.FIELD)));
			assertEquals(lastQty, new BigDecimal(historyMessage.getString(LastShares.FIELD)));
			assertEquals(lastPrice, new BigDecimal(historyMessage.getString(LastPx.FIELD)));
			assertEquals(cumQty, new BigDecimal(historyMessage.getString(CumQty.FIELD)));
			assertEquals(avgPrice, new BigDecimal(historyMessage.getString(AvgPx.FIELD)));
			assertEquals(symbol.getFullSymbol(), historyMessage.getString(Symbol.FIELD));
		}		

		{
			InternalID orderID2 = new InternalID("1001");
			InternalID clOrderID2 = new InternalID("1002");
			Message message2 = FIXMessageUtil.newExecutionReport(orderID2, clOrderID2, execID, execTransType, execType, ordStatus, side, orderQty, orderPrice, lastQty, lastPrice, leavesQty, cumQty, avgPrice, symbol);
			history.addIncomingMessage(message2);
			EventList<MessageHolder> historyList = history.getAllMessages();
			assertEquals(2, historyList.size());
			assertEquals(IncomingMessageHolder.class, historyList.get(1).getClass());
			IncomingMessageHolder holder = (IncomingMessageHolder) historyList.get(1);
			Message historyMessage = holder.getMessage();
			assertEquals(orderID2.toString(), historyMessage.getString(OrderID.FIELD));
			assertEquals(clOrderID2.toString(), historyMessage.getString(ClOrdID.FIELD));
			assertEquals(execID, historyMessage.getString(ExecID.FIELD));
			assertEquals(""+execTransType, historyMessage.getString(ExecTransType.FIELD));
			assertEquals(""+execType, historyMessage.getString(ExecType.FIELD));
			assertEquals(""+ordStatus, historyMessage.getString(OrdStatus.FIELD));
			assertEquals(""+side, historyMessage.getString(Side.FIELD));
			assertEquals(orderQty, new BigDecimal(historyMessage.getString(OrderQty.FIELD)));
			assertEquals(lastQty, new BigDecimal(historyMessage.getString(LastShares.FIELD)));
			assertEquals(lastPrice, new BigDecimal(historyMessage.getString(LastPx.FIELD)));
			assertEquals(cumQty, new BigDecimal(historyMessage.getString(CumQty.FIELD)));
			assertEquals(avgPrice, new BigDecimal(historyMessage.getString(AvgPx.FIELD)));
			assertEquals(symbol.getFullSymbol(), historyMessage.getString(Symbol.FIELD));
		}
	}

	/*
	 * Test method for 'org.marketcetera.photon.model.FIXMessageHistory.addOutgoingMessage(Message)'
	 */
	public void testAddOutgoingMessage() throws FieldNotFound {
		FIXMessageHistory history = getMessageHistory();
		InternalID orderID = new InternalID("1");
		char side = Side.SELL_SHORT_EXEMPT;
		BigDecimal quantity = new BigDecimal("2000");
		MSymbol symbol = new MSymbol("QWER");
		char timeInForce = TimeInForce.DAY;
		AccountID account = new AccountID("ACCT");
		Message message = FIXMessageUtil.newMarketOrder(orderID, side, quantity, symbol, timeInForce, account);
		history.addOutgoingMessage(message);

		EventList<MessageHolder> historyList = history.getAllMessages();
		assertEquals(1, historyList.size());
		assertEquals(OutgoingMessageHolder.class, historyList.get(0).getClass());
		OutgoingMessageHolder holder = (OutgoingMessageHolder) historyList.get(0);
		Message historyMessage = holder.getMessage();
		assertEquals(orderID.toString(), historyMessage.getString(ClOrdID.FIELD));
		assertEquals(""+side, historyMessage.getString(Side.FIELD));
		assertEquals(quantity, new BigDecimal(historyMessage.getString(OrderQty.FIELD)));
		assertEquals(symbol.getFullSymbol(), historyMessage.getString(Symbol.FIELD));
		assertEquals(""+timeInForce, historyMessage.getString(TimeInForce.FIELD));
		assertEquals(account.toString(), historyMessage.getString(Account.FIELD));
	}


	/*
	 * Test method for 'org.marketcetera.photon.model.FIXMessageHistory.getLatestExecutionReports()'
	 */
	public void testGetLatestExecutionReports() throws FieldNotFound {
		FIXMessageHistory history = getMessageHistory();
		Message order1 = FIXMessageUtil.newMarketOrder(new InternalID("1"), Side.BUY, new BigDecimal(1000), new MSymbol("ASDF"), TimeInForce.FILL_OR_KILL, new AccountID("ACCT"));
		Message executionReportForOrder1 = FIXMessageUtil.newExecutionReport(new InternalID("1001"), new InternalID("1"), "2001", ExecTransType.NEW, ExecType.NEW, OrdStatus.NEW, Side.BUY, new BigDecimal(1000), new BigDecimal(789), null, null, new BigDecimal(1000), BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("ASDF"));
		executionReportForOrder1.setField(new TransactTime(new Date(System.currentTimeMillis() - 10000)));
		Message order2 = FIXMessageUtil.newLimitOrder(new InternalID("3"), Side.SELL, new BigDecimal(2000), new MSymbol("QWER"), new BigDecimal("12.3"), TimeInForce.DAY, new AccountID("ACCT"));
		Message executionReportForOrder2 = FIXMessageUtil.newExecutionReport(new InternalID("1003"), new InternalID("3"), "2003", ExecTransType.NEW, ExecType.NEW, OrdStatus.NEW, Side.SELL, new BigDecimal(2000), new BigDecimal(789), null, null, new BigDecimal(2000), BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("QWER"));
		executionReportForOrder1.setField(new TransactTime(new Date(System.currentTimeMillis() - 8000)));
		Message secondExecutionReportForOrder1 = FIXMessageUtil.newExecutionReport(new InternalID("1001"), new InternalID("1"), "2004", ExecTransType.STATUS, ExecType.PARTIAL_FILL, OrdStatus.PARTIALLY_FILLED, Side.BUY, new BigDecimal(1000), new BigDecimal(789), new BigDecimal(100), new BigDecimal("11.5"), new BigDecimal(900), new BigDecimal(100), new BigDecimal("11.5"), new MSymbol("ASDF"));
		executionReportForOrder1.setField(new TransactTime(new Date(System.currentTimeMillis() - 7000)));

		history.addOutgoingMessage(order1);
		history.addIncomingMessage(executionReportForOrder1);
		history.addOutgoingMessage(order2);
		history.addIncomingMessage(executionReportForOrder2);
		history.addIncomingMessage(secondExecutionReportForOrder1);
		
		EventList<MessageHolder> historyList = history.getLatestExecutionReports();
		assertEquals(2, historyList.size());
		Message historyExecutionReportForOrder1 = historyList.get(0).getMessage();
		Message historyExecutionReportForOrder2 = historyList.get(1).getMessage();

		assertEquals("1001", historyExecutionReportForOrder1.getString(OrderID.FIELD));
		assertEquals("2004", historyExecutionReportForOrder1.getString(ExecID.FIELD));
		assertEquals(order1.getString(ClOrdID.FIELD), historyExecutionReportForOrder1.getString(ClOrdID.FIELD));
		assertEquals(order1.getString(Side.FIELD), historyExecutionReportForOrder1.getString(Side.FIELD));
		assertEquals(order1.getString(OrderQty.FIELD), historyExecutionReportForOrder1.getString(OrderQty.FIELD));
		assertEquals(order1.getString(Symbol.FIELD), historyExecutionReportForOrder1.getString(Symbol.FIELD));

		assertEquals("1003", historyExecutionReportForOrder2.getString(OrderID.FIELD));
		assertEquals("2003", historyExecutionReportForOrder2.getString(ExecID.FIELD));
		assertEquals(order2.getString(ClOrdID.FIELD), historyExecutionReportForOrder2.getString(ClOrdID.FIELD));
		assertEquals(order2.getString(Side.FIELD), historyExecutionReportForOrder2.getString(Side.FIELD));
		assertEquals(order2.getString(OrderQty.FIELD), historyExecutionReportForOrder2.getString(OrderQty.FIELD));
		assertEquals(order2.getString(Symbol.FIELD), historyExecutionReportForOrder2.getString(Symbol.FIELD));
}

	/*
	 * Test method for 'org.marketcetera.photon.model.FIXMessageHistory.addFIXMessageListener(IFIXMessageListener)'
	 */
	public void testAddFIXMessageListener() throws NoSuchFieldException, IllegalAccessException {
		FIXMessageHistory history = getMessageHistory();
		
		Message order1 = FIXMessageUtil.newMarketOrder(new InternalID("1"), Side.BUY, new BigDecimal(1000), new MSymbol("ASDF"), TimeInForce.FILL_OR_KILL, new AccountID("ACCT"));
		Message executionReportForOrder1 = FIXMessageUtil.newExecutionReport(new InternalID("1001"), new InternalID("1"), "2001", ExecTransType.NEW, ExecType.NEW, OrdStatus.NEW, Side.BUY, new BigDecimal(1000), new BigDecimal(789), null, null, new BigDecimal(1000), BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("ASDF"));

		ListEventListener<MessageHolder> fixMessageListener = new ListEventListener<MessageHolder>() {
			public int numIncomingMessages = 0;
			public int numOutgoingMessages = 0;

			public void listChanged(ListEvent<MessageHolder> event) {
				if (event.hasNext())
				{
					event.next();
					if (event.getType() == ListEvent.INSERT){
						EventList<MessageHolder> source = (EventList<MessageHolder>) event.getSource();
						int index = event.getIndex();
						MessageHolder holder = source.get(index);
						if (holder instanceof IncomingMessageHolder) {
							IncomingMessageHolder incoming = (IncomingMessageHolder) holder;
							try {
								assertEquals("1001", incoming.getMessage().getString(OrderID.FIELD));
								numIncomingMessages++;
							} catch (FieldNotFound e) {
								fail(e.getMessage());
							}
						} else if (holder instanceof OutgoingMessageHolder) {
							OutgoingMessageHolder outgoing = (OutgoingMessageHolder) holder;
							try {
								assertEquals("1", outgoing.getMessage().getString(ClOrdID.FIELD));
								numOutgoingMessages++;
							} catch (FieldNotFound e) {
								fail(e.getMessage());
							}
						}
					}	
				}
			}
			
		};
		history.getAllMessages().addListEventListener(fixMessageListener);
		
		history.addOutgoingMessage(order1);
		history.addIncomingMessage(executionReportForOrder1);
		//just use the AccessViolator to get the fields out of the anon inner class
		AccessViolator violator = new AccessViolator(fixMessageListener.getClass());
		assertEquals(1,violator.getField("numIncomingMessages", fixMessageListener));
		assertEquals(1,violator.getField("numOutgoingMessages", fixMessageListener));
	}

	/*
	 * Test method for 'org.marketcetera.photon.model.FIXMessageHistory.removePortfolioListener(IFIXMessageListener)'
	 */
	public void testRemovePortfolioListener() throws NoSuchFieldException, IllegalAccessException {
		FIXMessageHistory history = getMessageHistory();
		
		Message order1 = FIXMessageUtil.newMarketOrder(new InternalID("1"), Side.BUY, new BigDecimal(1000), new MSymbol("ASDF"), TimeInForce.FILL_OR_KILL, new AccountID("ACCT"));
		Message executionReportForOrder1 = FIXMessageUtil.newExecutionReport(new InternalID("1001"), new InternalID("1"), "2001", ExecTransType.NEW, ExecType.NEW, OrdStatus.NEW, Side.BUY, new BigDecimal(1000), new BigDecimal(789), null, null, new BigDecimal(1000), BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("ASDF"));

		ListEventListener<MessageHolder> fixMessageListener = new ListEventListener<MessageHolder>() {
			public int numIncomingMessages = 0;
			public int numOutgoingMessages = 0;

			public void listChanged(ListEvent<MessageHolder> event) {
				if (event.getType() == ListEvent.INSERT){
					Object source = event.getSource();
					if (source instanceof IncomingMessageHolder) {
						IncomingMessageHolder incoming = (IncomingMessageHolder) source;
						try {
							assertEquals("1001", incoming.getMessage().getString(OrderID.FIELD));
							numIncomingMessages++;
						} catch (FieldNotFound e) {
							fail(e.getMessage());
						}
					} else if (source instanceof OutgoingMessageHolder) {
						OutgoingMessageHolder outgoing = (OutgoingMessageHolder) source;
						try {
							assertEquals("1", outgoing.getMessage().getString(ClOrdID.FIELD));
							numOutgoingMessages++;
						} catch (FieldNotFound e) {
							fail(e.getMessage());
						}
					}

				}
			}
			
		};
		
		history.getAllMessages().addListEventListener(fixMessageListener);
		history.getAllMessages().removeListEventListener(fixMessageListener);
		
		history.addOutgoingMessage(order1);
		history.addIncomingMessage(executionReportForOrder1);
		//just use the AccessViolator to get the fields out of the anon inner class
		AccessViolator violator = new AccessViolator(fixMessageListener.getClass());
		assertEquals(0,violator.getField("numIncomingMessages", fixMessageListener));
		assertEquals(0,violator.getField("numOutgoingMessages", fixMessageListener));
	}
	
	
	public void testAveragePriceList() throws Exception {
		FIXMessageHistory messageHistory = getMessageHistory();
		InternalID orderID1 = new InternalID("1");
		InternalID clOrderID1 = new InternalID("2");
		String execID = "300";
		char execTransType = ExecTransType.STATUS;
		char execType = ExecType.PARTIAL_FILL;
		char ordStatus = OrdStatus.PARTIALLY_FILLED;
		char side = Side.SELL_SHORT;
		BigDecimal orderQty = new BigDecimal(1000);
		BigDecimal orderPrice = new BigDecimal(789);
		BigDecimal lastQty = new BigDecimal(100);
		BigDecimal lastPrice = new BigDecimal("12.3");
		BigDecimal leavesQty = new BigDecimal(900);
		BigDecimal cumQty = new BigDecimal("100");
		BigDecimal avgPrice = new BigDecimal("12.3");
		MSymbol symbol = new MSymbol("ASDF");

		Message message = FIXMessageUtil.newExecutionReport(orderID1, clOrderID1, execID, execTransType, execType, ordStatus, side, orderQty, orderPrice, lastQty, lastPrice, leavesQty, cumQty, avgPrice, symbol);
		messageHistory.addIncomingMessage(message);
		
		orderID1 = new InternalID("3");
		clOrderID1 = new InternalID("4");
		execID = "301";
		lastQty = new BigDecimal(900);
		lastPrice = new BigDecimal("12.4");
		cumQty = new BigDecimal(900);
		avgPrice = new BigDecimal("12.4");

		message = FIXMessageUtil.newExecutionReport(orderID1, clOrderID1, execID, execTransType, execType, ordStatus, side, orderQty, orderPrice, lastQty, lastPrice, leavesQty, cumQty, avgPrice, symbol);
		messageHistory.addIncomingMessage(message);
		
		EventList<MessageHolder> averagePriceList = messageHistory.getAveragePriceHistory();

		assertEquals(1, averagePriceList.size());
		
		IncomingMessageHolder holder = (IncomingMessageHolder) averagePriceList.get(0);
		Message returnedMessage = holder.getMessage();
		assertEquals(MsgType.EXECUTION_REPORT, returnedMessage.getHeader().getString(MsgType.FIELD));

		BigDecimal returnedAvgPrice = new BigDecimal(returnedMessage.getString(AvgPx.FIELD));
		assertTrue( new BigDecimal("1000").compareTo(new BigDecimal(returnedMessage.getString(CumQty.FIELD))) == 0);
		assertEquals( ((12.3*100)+(12.4*900))/1000, returnedAvgPrice.doubleValue(), .0001);
		
		
		orderID1 = new InternalID("4");
		clOrderID1 = new InternalID("5");
		execID = "302";
		lastQty = new BigDecimal(900);
		lastPrice = new BigDecimal("12.4");
		cumQty = new BigDecimal(900);
		avgPrice = new BigDecimal("12.4");
		side = Side.BUY;
		
		message = FIXMessageUtil.newExecutionReport(orderID1, clOrderID1, execID, execTransType, execType, ordStatus, side, orderQty, orderPrice, lastQty, lastPrice, leavesQty, cumQty, avgPrice, symbol);
		messageHistory.addIncomingMessage(message);

		assertEquals(2, messageHistory.getAveragePriceHistory().size());
		holder = (IncomingMessageHolder) averagePriceList.get(0);
		returnedMessage = holder.getMessage();
		assertEquals(MsgType.EXECUTION_REPORT, returnedMessage.getHeader().getString(MsgType.FIELD));

		returnedAvgPrice = new BigDecimal(returnedMessage.getString(AvgPx.FIELD));
		assertEquals(Side.BUY, returnedMessage.getChar(Side.FIELD));
		assertEquals( 12.4, returnedAvgPrice.doubleValue(), .0001);
		assertTrue( new BigDecimal("900").compareTo(new BigDecimal(returnedMessage.getString(CumQty.FIELD))) == 0);
	}
}
