package org.marketcetera.photon.model;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.marketcetera.core.AccessViolator;
import org.marketcetera.core.AccountID;
import org.marketcetera.core.InternalID;
import org.marketcetera.quickfix.FIXMessageUtil;

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
import quickfix.field.OrdStatus;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;

public class FIXMessageHistoryTest extends TestCase {

	/*
	 * Test method for 'org.marketcetera.photon.model.FIXMessageHistory.addIncomingMessage(Message)'
	 */
	public void testAddIncomingMessage() throws FieldNotFound {
		FIXMessageHistory history = new FIXMessageHistory();
		InternalID orderID1 = new InternalID("1");
		InternalID clOrderID1 = new InternalID("2");
		String execID = "3";
		char execTransType = ExecTransType.STATUS;
		char execType = ExecType.PARTIAL_FILL;
		char ordStatus = OrdStatus.PARTIALLY_FILLED;
		char side = Side.SELL_SHORT;
		BigDecimal orderQty = new BigDecimal(1000);
		BigDecimal lastQty = new BigDecimal(100);
		BigDecimal lastPrice = new BigDecimal("12.3");
		BigDecimal leavesQty = new BigDecimal(900);
		BigDecimal cumQty = new BigDecimal(100);
		BigDecimal avgPrice = new BigDecimal("12.3");
		String symbol = "ASDF";
		
		Message message = FIXMessageUtil.newExecutionReport(orderID1, clOrderID1, execID, execTransType, execType, ordStatus, side, orderQty, lastQty, lastPrice, leavesQty, cumQty, avgPrice, symbol);

		{
			history.addIncomingMessage(message);
			Object [] historyArray = history.getHistory();
			assertEquals(1, historyArray.length);
			assertEquals(FIXMessageHistory.IncomingMessageHolder.class, historyArray[0].getClass());
			FIXMessageHistory.IncomingMessageHolder holder = (FIXMessageHistory.IncomingMessageHolder) historyArray[0];
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
			assertEquals(symbol, historyMessage.getString(Symbol.FIELD));
		}		

		{
			InternalID orderID2 = new InternalID("1001");
			InternalID clOrderID2 = new InternalID("1002");
			Message message2 = FIXMessageUtil.newExecutionReport(orderID2, clOrderID2, execID, execTransType, execType, ordStatus, side, orderQty, lastQty, lastPrice, leavesQty, cumQty, avgPrice, symbol);
			history.addIncomingMessage(message2);
			Object [] historyArray = history.getHistory();
			assertEquals(2, historyArray.length);
			assertEquals(FIXMessageHistory.IncomingMessageHolder.class, historyArray[1].getClass());
			FIXMessageHistory.IncomingMessageHolder holder = (FIXMessageHistory.IncomingMessageHolder) historyArray[1];
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
			assertEquals(symbol, historyMessage.getString(Symbol.FIELD));
		}
	}

	/*
	 * Test method for 'org.marketcetera.photon.model.FIXMessageHistory.addOutgoingMessage(Message)'
	 */
	public void testAddOutgoingMessage() throws FieldNotFound {
		FIXMessageHistory history = new FIXMessageHistory();
		InternalID orderID = new InternalID("1");
		char side = Side.SELL_SHORT_EXEMPT;
		BigDecimal quantity = new BigDecimal("2000");
		String symbol = "QWER";
		char timeInForce = TimeInForce.DAY;
		AccountID account = new AccountID("ACCT");
		Message message = FIXMessageUtil.newMarketOrder(orderID, side, quantity, symbol, timeInForce, account);
		history.addOutgoingMessage(message);

		Object [] historyArray = history.getHistory();
		assertEquals(1, historyArray.length);
		assertEquals(FIXMessageHistory.OutgoingMessageHolder.class, historyArray[0].getClass());
		FIXMessageHistory.OutgoingMessageHolder holder = (FIXMessageHistory.OutgoingMessageHolder) historyArray[0];
		Message historyMessage = holder.getMessage();
		assertEquals(orderID.toString(), historyMessage.getString(ClOrdID.FIELD));
		assertEquals(""+side, historyMessage.getString(Side.FIELD));
		assertEquals(quantity, new BigDecimal(historyMessage.getString(OrderQty.FIELD)));
		assertEquals(symbol, historyMessage.getString(Symbol.FIELD));
		assertEquals(""+timeInForce, historyMessage.getString(TimeInForce.FIELD));
		assertEquals(account.toString(), historyMessage.getString(Account.FIELD));
	}


	/*
	 * Test method for 'org.marketcetera.photon.model.FIXMessageHistory.getLatestExecutionReports()'
	 */
	public void testGetLatestExecutionReports() throws FieldNotFound {
		FIXMessageHistory history = new FIXMessageHistory();
		Message order1 = FIXMessageUtil.newMarketOrder(new InternalID("1"), Side.BUY, new BigDecimal(1000), "ASDF", TimeInForce.FILL_OR_KILL, new AccountID("ACCT"));
		Message executionReportForOrder1 = FIXMessageUtil.newExecutionReport(new InternalID("1001"), new InternalID("1"), "2001", ExecTransType.NEW, ExecType.NEW, OrdStatus.NEW, Side.BUY, new BigDecimal(1000), null, null, new BigDecimal(1000), BigDecimal.ZERO, BigDecimal.ZERO, "ASDF");
		Message order2 = FIXMessageUtil.newLimitOrder(new InternalID("3"), Side.SELL, new BigDecimal(2000), "QWER", new BigDecimal("12.3"), TimeInForce.DAY, new AccountID("ACCT"));
		Message executionReportForOrder2 = FIXMessageUtil.newExecutionReport(new InternalID("1003"), new InternalID("3"), "2003", ExecTransType.NEW, ExecType.NEW, OrdStatus.NEW, Side.SELL, new BigDecimal(2000), null, null, new BigDecimal(2000), BigDecimal.ZERO, BigDecimal.ZERO, "QWER");
		Message secondExecutionReportForOrder1 = FIXMessageUtil.newExecutionReport(new InternalID("1001"), new InternalID("1"), "2004", ExecTransType.STATUS, ExecType.PARTIAL_FILL, OrdStatus.PARTIALLY_FILLED, Side.BUY, new BigDecimal(1000), new BigDecimal(100), new BigDecimal("11.5"), new BigDecimal(900), new BigDecimal(100), new BigDecimal("11.5"), "ASDF");

		history.addOutgoingMessage(order1);
		history.addIncomingMessage(executionReportForOrder1);
		history.addOutgoingMessage(order2);
		history.addIncomingMessage(executionReportForOrder2);
		history.addIncomingMessage(secondExecutionReportForOrder1);
		
		Object[] historyArray = history.getLatestExecutionReports();
		assertEquals(2, historyArray.length);
		assertTrue(historyArray[0] instanceof Message);
		assertTrue(historyArray[1] instanceof Message);
		Message historyExecutionReportForOrder1 = (Message)historyArray[0];
		Message historyExecutionReportForOrder2 = (Message)historyArray[1];

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
		FIXMessageHistory history = new FIXMessageHistory();
		
		Message order1 = FIXMessageUtil.newMarketOrder(new InternalID("1"), Side.BUY, new BigDecimal(1000), "ASDF", TimeInForce.FILL_OR_KILL, new AccountID("ACCT"));
		Message executionReportForOrder1 = FIXMessageUtil.newExecutionReport(new InternalID("1001"), new InternalID("1"), "2001", ExecTransType.NEW, ExecType.NEW, OrdStatus.NEW, Side.BUY, new BigDecimal(1000), null, null, new BigDecimal(1000), BigDecimal.ZERO, BigDecimal.ZERO, "ASDF");

		IFIXMessageListener fixMessageListener = new IFIXMessageListener() {
			public int numIncomingMessages = 0;
			public int numOutgoingMessages = 0;
			public void incomingMessage(Message message) {
				try {
					assertEquals("1001", message.getString(OrderID.FIELD));
					numIncomingMessages++;
				} catch (FieldNotFound e) {
					fail(e.getMessage());
				}
			}
			
			public void outgoingMessage(Message message) {
				try {
					assertEquals("1", message.getString(ClOrdID.FIELD));
					numOutgoingMessages++;
				} catch (FieldNotFound e) {
					fail(e.getMessage());
				}
			}
			
		};
		history.addFIXMessageListener(fixMessageListener);
		
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
		FIXMessageHistory history = new FIXMessageHistory();
		
		Message order1 = FIXMessageUtil.newMarketOrder(new InternalID("1"), Side.BUY, new BigDecimal(1000), "ASDF", TimeInForce.FILL_OR_KILL, new AccountID("ACCT"));
		Message executionReportForOrder1 = FIXMessageUtil.newExecutionReport(new InternalID("1001"), new InternalID("1"), "2001", ExecTransType.NEW, ExecType.NEW, OrdStatus.NEW, Side.BUY, new BigDecimal(1000), null, null, new BigDecimal(1000), BigDecimal.ZERO, BigDecimal.ZERO, "ASDF");

		IFIXMessageListener fixMessageListener = new IFIXMessageListener() {
			public int numIncomingMessages = 0;
			public int numOutgoingMessages = 0;
			public void incomingMessage(Message message) {
				try {
					assertEquals("1001", message.getString(OrderID.FIELD));
					numIncomingMessages++;
				} catch (FieldNotFound e) {
					fail(e.getMessage());
				}
			}
			
			public void outgoingMessage(Message message) {
				try {
					assertEquals("1", message.getString(ClOrdID.FIELD));
					numOutgoingMessages++;
				} catch (FieldNotFound e) {
					fail(e.getMessage());
				}
			}
			
		};
		history.addFIXMessageListener(fixMessageListener);
		history.removeFIXMessageListener(fixMessageListener);
		
		history.addOutgoingMessage(order1);
		history.addIncomingMessage(executionReportForOrder1);
		//just use the AccessViolator to get the fields out of the anon inner class
		AccessViolator violator = new AccessViolator(fixMessageListener.getClass());
		assertEquals(0,violator.getField("numIncomingMessages", fixMessageListener));
		assertEquals(0,violator.getField("numOutgoingMessages", fixMessageListener));
	}

}
